package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.service.MercadoPagoService;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Value("${mercadopago.public.key}")
    private String publicKey;

    @Value("${mercadopago.environment}")
    private String environment;

    @Override
    public PreferenceResponse createPreference(CreatePreferenceRequest request) throws MPException, MPApiException {
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
        // Implementar consulta de estado de pago si es necesario
        return "pending";
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }
}
