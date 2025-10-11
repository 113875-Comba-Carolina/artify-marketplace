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
import java.util.List;
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
            System.out.println("=== DEBUG: Creando preferencia ===");
            System.out.println("Items recibidos: " + request.getItems().size());
            
            // Crear cliente de preferencias
            PreferenceClient client = new PreferenceClient();
            
            // Construir items
            List<PreferenceItemRequest> items = new ArrayList<>();
            for (ItemRequest item : request.getItems()) {
                System.out.println("=== DEBUG: Procesando item ===");
                System.out.println("Title: " + item.getTitle());
                System.out.println("Description: " + item.getDescription());
                System.out.println("Quantity: " + item.getQuantity());
                System.out.println("Unit Price: " + item.getUnitPrice());
                System.out.println("Picture URL: " + item.getPictureUrl());
                System.out.println("Category ID: " + item.getCategoryId());
                
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(new BigDecimal(item.getUnitPrice()))
                        .pictureUrl(item.getPictureUrl())
                        .categoryId(item.getCategoryId())
                        .build();
                items.add(itemRequest);
                System.out.println("Item creado exitosamente");
            }
            
            // Construir preferencia
            PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(request.getExternalReference())
                    .notificationUrl(request.getNotificationUrl());
            
            // Agregar URLs de retorno si están definidas
            System.out.println("=== DEBUG: Configurando URLs de retorno ===");
            System.out.println("Success URL: " + request.getSuccessUrl());
            System.out.println("Failure URL: " + request.getFailureUrl());
            System.out.println("Pending URL: " + request.getPendingUrl());
            
            if (request.getSuccessUrl() != null || request.getFailureUrl() != null || request.getPendingUrl() != null) {
                PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                        .success(request.getSuccessUrl())
                        .failure(request.getFailureUrl())
                        .pending(request.getPendingUrl())
                        .build();
                builder.backUrls(backUrls);
                System.out.println("Back URLs configuradas exitosamente");
            }
            
            // NO configuramos auto return para evitar el error de validación
            System.out.println("=== DEBUG: Auto return NO configurado ===");
            System.out.println("Auto Return solicitado: " + request.getAutoReturn());
            System.out.println("Success URL disponible: " + (request.getSuccessUrl() != null));
            System.out.println("Auto return NO configurado para evitar error de validación de Mercado Pago");
            
            PreferenceRequest preferenceRequest = builder.build();
            
            System.out.println("=== DEBUG: Creando preferencia en Mercado Pago ===");
            System.out.println("PreferenceRequest: " + preferenceRequest);
            
            // Crear preferencia
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("=== DEBUG: Preferencia creada exitosamente ===");
            System.out.println("ID: " + preference.getId());
            System.out.println("Init Point: " + preference.getInitPoint());
            
            // Crear orden en la base de datos
            System.out.println("=== DEBUG: Creando orden en la base de datos ===");
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
            
            Orden orden = ordenService.crearOrden(usuario, request.getExternalReference(), carritoItems);
            System.out.println("=== DEBUG: Orden creada con ID: " + orden.getId() + " ===");
            
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
            System.out.println("=== CONSULTANDO PAGO POR EXTERNAL_REFERENCE ===");
            System.out.println("External Reference: " + externalReference);
            
            // Buscar la orden en la base de datos
            Orden orden = ordenService.obtenerOrdenPorExternalReference(externalReference);
            if (orden == null) {
                System.out.println("No se encontró orden con external_reference: " + externalReference);
                return "orden_no_encontrada";
            }
            
            System.out.println("Orden encontrada: " + orden.getId());
            System.out.println("Estado actual: " + orden.getEstado());
            
            // Si ya tiene mercadoPagoId, consultar el estado en Mercado Pago
            if (orden.getMercadoPagoId() != null) {
                System.out.println("Consultando estado en Mercado Pago...");
                String status = getPaymentStatus(orden.getMercadoPagoId());
                System.out.println("Estado en Mercado Pago: " + status);
                return status;
            } else {
                System.out.println("La orden no tiene mercadoPagoId asignado");
                return "sin_mercado_pago_id";
            }
            
        } catch (Exception e) {
            System.out.println("Error consultando pago por external_reference: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("=== PROCESANDO NOTIFICACIÓN ===");
            System.out.println("Notificación: " + notification);
            
            // Parsear la notificación JSON
            JsonNode notificationNode = objectMapper.readTree(notification);
            
            // Extraer el tipo de notificación
            String type = notificationNode.get("type").asText();
            System.out.println("Tipo de notificación: " + type);
            
            if ("payment".equals(type)) {
                // Obtener el ID del pago
                String paymentId = notificationNode.get("data").get("id").asText();
                System.out.println("Payment ID: " + paymentId);
                
                // Verificar si es una notificación de prueba
                // Los IDs de prueba suelen ser números pequeños o contienen "123456"
                if ("123456".equals(paymentId) || paymentId.startsWith("123456") || 
                    paymentId.length() < 10 || paymentId.matches("\\d{4,8}")) {
                    System.out.println("=== NOTIFICACIÓN DE PRUEBA DETECTADA ===");
                    System.out.println("Payment ID: " + paymentId);
                    System.out.println("Esta es una notificación de prueba de Mercado Pago");
                    System.out.println("El webhook está funcionando correctamente");
                    System.out.println("En una transacción real, aquí se procesaría el pago");
                    return; // Salir sin procesar más
                }
                
                try {
                    // Obtener los detalles del pago desde Mercado Pago
                    PaymentClient paymentClient = new PaymentClient();
                    Payment payment = paymentClient.get(Long.parseLong(paymentId));
                    
                    if (payment != null) {
                        System.out.println("=== DETALLES DEL PAGO ===");
                        System.out.println("ID: " + payment.getId());
                        System.out.println("Status: " + payment.getStatus());
                        System.out.println("External Reference: " + payment.getExternalReference());
                        
                        // Buscar la orden por external_reference
                        String externalReference = payment.getExternalReference();
                        if (externalReference != null) {
                            Orden orden = ordenService.obtenerOrdenPorExternalReference(externalReference);
                            if (orden != null) {
                                System.out.println("=== ACTUALIZANDO ORDEN ===");
                                System.out.println("Orden encontrada: " + orden.getId());
                                
                                // Actualizar el mercadoPagoId si no está establecido
                                if (orden.getMercadoPagoId() == null) {
                                    orden.setMercadoPagoId(payment.getId().toString());
                                }
                                
                                // Actualizar el estado de la orden
                                ordenService.actualizarEstadoOrden(orden.getMercadoPagoId(), payment.getStatus());
                                
                                System.out.println("Orden actualizada exitosamente");
                            } else {
                                System.out.println("No se encontró orden con external_reference: " + externalReference);
                            }
                        } else {
                            System.out.println("No se encontró external_reference en el pago");
                        }
                    } else {
                        System.out.println("No se pudo obtener el pago desde Mercado Pago");
                    }
                } catch (MPApiException e) {
                    System.out.println("=== ERROR AL OBTENER PAGO ===");
                    System.out.println("Status: " + e.getApiResponse().getStatusCode());
                    System.out.println("Content: " + e.getApiResponse().getContent());
                    System.out.println("Message: " + e.getMessage());
                    System.out.println("Esto puede ocurrir si el pago no existe o hay un problema de autenticación");
                } catch (Exception e) {
                    System.out.println("Error inesperado al obtener pago: " + e.getMessage());
                    e.printStackTrace();
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
            System.out.println("=== LISTANDO ÓRDENES PARA DEBUG ===");
            
            // Obtener todas las órdenes
            List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
            
            System.out.println("Total de órdenes encontradas: " + ordenes.size());
            
            for (Orden orden : ordenes) {
                System.out.println("=== ORDEN ===");
                System.out.println("ID: " + orden.getId());
                System.out.println("External Reference: '" + orden.getExternalReference() + "'");
                System.out.println("External Reference Length: " + (orden.getExternalReference() != null ? orden.getExternalReference().length() : "null"));
                System.out.println("Estado: " + orden.getEstado());
                System.out.println("MercadoPago ID: " + orden.getMercadoPagoId());
                System.out.println("Total: " + orden.getTotal());
                System.out.println("Fecha: " + orden.getFechaCreacion());
                System.out.println("Usuario: " + (orden.getUsuario() != null ? orden.getUsuario().getEmail() : "null"));
                System.out.println("========================");
            }
            
            return ordenes;
            
        } catch (Exception e) {
            System.out.println("Error listando órdenes: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
