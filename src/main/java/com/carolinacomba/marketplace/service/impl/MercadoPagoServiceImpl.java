package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.ItemOrden;
import com.carolinacomba.marketplace.model.Orden;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.EmailService;
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

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final OrdenService ordenService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PreferenceResponse createPreference(CreatePreferenceRequest request, Usuario usuario) throws MPException, MPApiException {
        try {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            
            PreferenceClient client = new PreferenceClient();
            
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
            
            PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(request.getExternalReference())
                    .notificationUrl(request.getNotificationUrl());
            
            if (request.getSuccessUrl() != null || request.getFailureUrl() != null || request.getPendingUrl() != null) {
                PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                        .success(request.getSuccessUrl())
                        .failure(request.getFailureUrl())
                        .pending(request.getPendingUrl())
                        .build();
                builder.backUrls(backUrls);
            }
            
            // NO configurar autoReturn para mantener el botón "Volver a la tienda"
            // El autoReturn hace que MercadoPago redirija automáticamente sin mostrar el botón
            // Solo configurar autoReturn si es explícitamente true
            if (request.getAutoReturn() != null && request.getAutoReturn()) {
                builder.autoReturn("approved");
            }

            PreferenceRequest preferenceRequest = builder.build();

            Preference preference = client.create(preferenceRequest);

            List<CarritoItem> carritoItems = request.getItems().stream()
                    .map(item -> CarritoItem.builder()
                            .productoId(0L)
                            .nombreProducto(item.getTitle())
                            .imagenUrl(item.getPictureUrl())
                            .categoria(item.getCategoryId())
                            .cantidad(item.getQuantity())
                            .precio(BigDecimal.valueOf(item.getUnitPrice()))
                            .build())
                    .collect(Collectors.toList());
            
            // Verificar si ya existe una orden con este externalReference
            Orden ordenExistente = ordenService.obtenerOrdenPorExternalReference(request.getExternalReference());
            
            if (ordenExistente == null) {
                // Solo crear orden si no existe (flujo del carrito)
                ordenService.crearOrden(usuario, request.getExternalReference(), carritoItems);
            }
            
            PreferenceResponse response = new PreferenceResponse();
            response.setId(preference.getId());
            response.setInitPoint(preference.getInitPoint());
            response.setSandboxInitPoint(preference.getSandboxInitPoint());
            response.setSuccess(true);
            response.setMessage("Preferencia creada exitosamente");
            
            return response;
            
        } catch (MPApiException e) {
            e.printStackTrace();
            
            PreferenceResponse response = new PreferenceResponse();
            response.setSuccess(false);
            response.setMessage("Error de API: " + e.getMessage());
            return response;
        } catch (MPException e) {
            e.printStackTrace();
            
            PreferenceResponse response = new PreferenceResponse();
            response.setSuccess(false);
            response.setMessage("Error de Mercado Pago: " + e.getMessage());
            return response;
        } catch (Exception e) {
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
            com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
            
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentId));
            return payment != null ? payment.getStatus() : "not_found";
        } catch (Exception e) {
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
            
            if (orden.getMercadoPagoId() != null) {
                String status = getPaymentStatus(orden.getMercadoPagoId());
                return status;
            } else {
                return "sin_mercado_pago_id";
            }
            
        } catch (Exception e) {
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
            JsonNode notificationNode = objectMapper.readTree(notification);
            
            if (notificationNode.get("type") == null) {
                return;
            }
            
            String type = notificationNode.get("type").asText();
            
            if ("payment".equals(type)) {
                if (notificationNode.get("data") == null || notificationNode.get("data").get("id") == null) {
                    return;
                }
                
                String paymentId = notificationNode.get("data").get("id").asText();
                
                if ("123456".equals(paymentId) || paymentId.startsWith("123456")) {
                    return;
                }
                
                try {
                    com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
                    
                    PaymentClient paymentClient = new PaymentClient();
                    Payment payment = paymentClient.get(Long.parseLong(paymentId));
                    
                    if (payment != null) {
                        String externalReference = payment.getExternalReference();
                        if (externalReference != null) {
                            Orden orden = ordenService.obtenerOrdenPorExternalReference(externalReference);
                            if (orden != null) {
                                if (orden.getMercadoPagoId() == null) {
                                    orden.setMercadoPagoId(payment.getId().toString());
                                }
                                
                                ordenService.actualizarEstadoOrden(orden.getMercadoPagoId(), payment.getStatus());
                                
                                if ("approved".equals(payment.getStatus())) {
                                    ordenService.reducirStockProductos(orden.getId());
                                    
                                    // Enviar email de confirmación de orden al comprador
                                    try {
                                        OrderConfirmationEmailData orderData = new OrderConfirmationEmailData(
                                            orden.getUsuario().getNombre(),
                                            orden.getUsuario().getEmail(),
                                            orden.getId(),
                                            orden.getFechaCreacion(),
                                            orden.getTotal(),
                                            convertirItemsParaEmail(orden.getItems()),
                                            "Dirección de envío pendiente", // TODO: Agregar campo direccionEnvio a Orden
                                            "CONFIRMADA"
                                        );
                                        emailService.sendOrderConfirmationEmail(orderData);
                                    } catch (Exception e) {
                                        System.err.println("Error enviando email de confirmación: " + e.getMessage());
                                    }

                                    // Enviar email de notificación a los artesanos
                                    try {
                                        enviarNotificacionesAVendedores(orden);
                                    } catch (Exception e) {
                                        System.err.println("Error enviando notificaciones a vendedores: " + e.getMessage());
                                    }
                                }
                                
                            }
                        }
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object listarOrdenesParaDebug() {
        try {
            List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
            
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
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Convierte los items de la orden al formato necesario para el email
     */
    private List<OrderConfirmationEmailData.OrderItemData> convertirItemsParaEmail(List<com.carolinacomba.marketplace.model.ItemOrden> items) {
        return items.stream()
            .map(item -> new OrderConfirmationEmailData.OrderItemData(
                item.getProducto().getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getProducto().getUsuario().getNombre() // El usuario que creó el producto
            ))
            .collect(Collectors.toList());
    }

    /**
     * Envía notificaciones de nueva venta a los artesanos
     */
    private void enviarNotificacionesAVendedores(Orden orden) {
        // Agrupar items por artesano (usuario que creó el producto)
        Map<String, List<ItemOrden>> itemsPorArtesano = orden.getItems()
            .stream()
            .collect(Collectors.groupingBy(item -> item.getProducto().getUsuario().getEmail()));

        // Enviar notificación a cada artesano
        itemsPorArtesano.forEach((emailArtesano, items) -> {
            try {
                Usuario artesano = items.get(0).getProducto().getUsuario();
                String productoNombre = items.get(0).getProducto().getNombre();
                Integer cantidad = items.stream()
                    .mapToInt(ItemOrden::getCantidad)
                    .sum();

                emailService.sendNewSaleNotificationEmail(
                    emailArtesano,
                    artesano.getNombre(),
                    productoNombre,
                    cantidad,
                    orden.getUsuario().getNombre()
                );
            } catch (Exception e) {
                System.err.println("Error enviando notificación de venta a " + emailArtesano + ": " + e.getMessage());
            }
        });
    }
}
