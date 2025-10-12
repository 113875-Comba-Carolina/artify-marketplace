package com.carolinacomba.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequest {
    
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    private Double amount;
    
    @NotBlank(message = "La descripción es requerida")
    private String description;
    
    @NotBlank(message = "El email del pagador es requerido")
    private String payerEmail;
    
    private String externalReference;
    
    private String notificationUrl;
    
    private String successUrl;
    
    private String failureUrl;
    
    private String pendingUrl;
}
