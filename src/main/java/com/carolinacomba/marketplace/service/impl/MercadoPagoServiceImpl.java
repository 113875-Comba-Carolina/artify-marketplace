package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.Orden;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.MercadoPagoService;
import com.carolinacomba.marketplace.service.OrdenService;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Value("${mercadopago.public.key}")
    private String publicKey;

    @Value("${mercadopago.environment}")
    private String environment;

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final OrdenService ordenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PreferenceResponse createPreference(CreatePreferenceRequest request, Usuario usuario) throws MPException, MPApiException {
        try {
            // Configurar el access token
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            
            // Crear cliente de preferencias
            PreferenceClient client = new PreferenceClient();
            
            // Construir items
            List<PreferenceItemRequest> items = new ArrayList<>();
            for (ItemRequest item : request.getItems()) {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(new BigDecimal(item.getUnitPrice()))
                        .pictureUrl(item.getPictureUrl())
                        .categoryId(item.getCategoryId())
                        .build();
                items.add(itemRequest);
            }
            
            // Construir preferencia
            PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(request.getExternalReference())
                    .notificationUrl(request.getNotificationUrl());
            
            // Agregar URLs de retorno si están definidas
            if (request.getSuccessUrl() != null || request.getFailureUrl() != null || request.getPendingUrl() != null) {
                PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                        .success(request.getSuccessUrl())
                        .failure(request.getFailureUrl())
                        .pending(request.getPendingUrl())
                        .build();
                builder.backUrls(backUrls);
            }
            
            PreferenceRequest preferenceRequest = builder.build();
            
            // Crear preferencia
            Preference preference = client.create(preferenceRequest);
            
            // Crear orden en la base de datos
            List<CarritoItem> carritoItems = request.getItems().stream()
                    .map(item -> CarritoItem.builder()
                            .productoId(0L) // Por ahora usamos 0, se puede mejorar después
                            .nombreProducto(item.getTitle())
                            .imagenUrl(item.getPictureUrl())
                            .categoria(item.getCategoryId())
                            .cantidad(item.getQuantity())
                            .precio(BigDecimal.valueOf(item.getUnitPrice()))
                            .build())
                    .collect(Collectors.toList());
            
            ordenService.crearOrden(usuario, request.getExternalReference(), carritoItems);
            
            // Construir respuesta
            PreferenceResponse response = new PreferenceResponse();
            response.setId(preference.getId());
            response.setInitPoint(preference.getInitPoint());
            response.setSandboxInitPoint(preference.getSandboxInitPoint());
            response.setSuccess(true);
            response.setMessage("Preferencia creada exitosamente");
            
            return response;
            
        } catch (MPApiException e) {
            System.out.println("=== ERROR MPApiException ===");
            System.out.println("Status: " + e.getApiResponse().getStatusCode());
            System.out.println("Content: " + e.getApiResponse().getContent());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            PreferenceResponse response = new PreferenceResponse();
            response.setSuccess(false);
            response.setMessage("Error de API: " + e.getMessage());
            return response;
        } catch (MPException e) {
            System.out.println("=== ERROR MPException ===");
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            PreferenceResponse response = new PreferenceResponse();
            response.setSuccess(false);
            response.setMessage("Error de Mercado Pago: " + e.getMessage());
            return response;
        } catch (Exception e) {
            System.out.println("=== ERROR Exception ===");
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            PreferenceResponse response = new PreferenceResponse();
            response.setSuccess(false);
            response.setMessage("Error interno: " + e.getMessage());
            return response;
        }
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        // Para Checkout Pro, no necesitamos crear pagos directos
        // Solo creamos preferencias
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setMessage("Para Checkout Pro, use createPreference en lugar de createPayment");
        return response;
    }

    @Override
    public String getPaymentStatus(String paymentId) {
        try {
            // Configurar el access token
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId));
            return payment != null ? payment.getStatus() : "not_found";
        } catch (Exception e) {
            System.out.println("Error consultando pago: " + e.getMessage());
            return "error";
        }
    }

    @Override
    public String getPaymentStatusByReference(String externalReference) {
        try {
            // Buscar la orden en la base de datos
            Orden orden = ordenService.obtenerOrdenPorExternalReference(externalReference);
            if (orden == null) {
                return "orden_no_encontrada";
            }
            
            // Si ya tiene mercadoPagoId, consultar el estado en Mercado Pago
            if (orden.getMercadoPagoId() != null) {
                String status = getPaymentStatus(orden.getMercadoPagoId());
                return status;
            } else {
                return "sin_mercado_pago_id";
            }
            
        } catch (Exception e) {
            System.out.println("Error consultando pago por external_reference: " + e.getMessage());
            return "error";
        }
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public void procesarNotificacion(String notification) {
        try {
            // Parsear la notificación JSON
            JsonNode notificationNode = objectMapper.readTree(notification);
            
            // Extraer el tipo de notificación
            String type = notificationNode.get("type").asText();
            
            if ("payment".equals(type)) {
                // Obtener el ID del pago
                String paymentId = notificationNode.get("data").get("id").asText();
                
                // Verificar si es una notificación de prueba (solo para IDs muy específicos)
                if ("123456".equals(paymentId) || paymentId.startsWith("123456")) {
                    System.out.println("Webhook: Notificación de prueba recibida (ID: " + paymentId + ")");
                    return; // Salir sin procesar más
                }
                
                try {
                    // Configurar el access token
                    com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
                    
                    // Obtener los detalles del pago desde Mercado Pago
                    PaymentClient paymentClient = new PaymentClient();
                    Payment payment = paymentClient.get(Long.parseLong(paymentId));
                    
                    if (payment != null) {
                        // Buscar la orden por external_reference
                        String externalReference = payment.getExternalReference();
                        if (externalReference != null) {
                            Orden orden = ordenService.obtenerOrdenPorExternalReference(externalReference);
                            if (orden != null) {
                                // Actualizar el mercadoPagoId si no está establecido
                                if (orden.getMercadoPagoId() == null) {
                                    orden.setMercadoPagoId(payment.getId().toString());
                                }
                                
                                // Actualizar el estado de la orden
                                ordenService.actualizarEstadoOrden(orden.getMercadoPagoId(), payment.getStatus());
                                
                                // Si el pago fue aprobado, reducir el stock de los productos
                                if ("approved".equals(payment.getStatus())) {
                                    ordenService.reducirStockProductos(orden.getId());
                                    System.out.println("Webhook: Stock reducido para orden " + orden.getId());
                                }
                                
                                System.out.println("Webhook: Orden " + orden.getId() + " actualizada a estado " + payment.getStatus());
                            } else {
                                System.out.println("Webhook: No se encontró orden con external_reference: " + externalReference);
                            }
                        } else {
                            System.out.println("Webhook: No se encontró external_reference en el pago");
                        }
                    } else {
                        System.out.println("Webhook: No se pudo obtener el pago desde Mercado Pago");
                    }
                } catch (MPApiException e) {
                    System.out.println("Webhook: Error al obtener pago - " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Webhook: Error inesperado - " + e.getMessage());
                }
            } else {
                System.out.println("Tipo de notificación no manejado: " + type);
            }
            
        } catch (Exception e) {
            System.out.println("Error procesando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void configureWebhook(String webhookUrl) {
        try {
            System.out.println("=== CONFIGURANDO WEBHOOK ===");
            System.out.println("URL del webhook: " + webhookUrl);
            
            // Nota: La configuración del webhook debe hacerse manualmente en el dashboard de Mercado Pago
            // o usando la API de Mercado Pago para configurar webhooks
            System.out.println("IMPORTANTE: Debes configurar el webhook manualmente en:");
            System.out.println("1. Ir a https://www.mercadopago.com.ar/developers/panel/credentials");
            System.out.println("2. Seleccionar tu aplicación");
            System.out.println("3. Ir a la sección 'Webhooks'");
            System.out.println("4. Agregar la URL: " + webhookUrl);
            System.out.println("5. Seleccionar los eventos: 'payment'");
            
        } catch (Exception e) {
            System.out.println("Error configurando webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Object listarOrdenesParaDebug() {
        try {
            // Obtener todas las órdenes
            List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
            
            // Crear una lista simple para debug
            List<Map<String, Object>> ordenesDebug = new ArrayList<>();
            for (Orden orden : ordenes) {
                Map<String, Object> ordenInfo = new HashMap<>();
                ordenInfo.put("id", orden.getId());
                ordenInfo.put("externalReference", orden.getExternalReference());
                ordenInfo.put("estado", orden.getEstado());
                ordenInfo.put("mercadoPagoId", orden.getMercadoPagoId());
                ordenInfo.put("total", orden.getTotal());
                ordenInfo.put("fechaCreacion", orden.getFechaCreacion());
                ordenInfo.put("usuario", orden.getUsuario() != null ? orden.getUsuario().getEmail() : "null");
                ordenInfo.put("itemsCount", orden.getItems() != null ? orden.getItems().size() : 0);
                ordenesDebug.add(ordenInfo);
            }
            
            return ordenesDebug;
        } catch (Exception e) {
            System.out.println("Error listando órdenes: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
