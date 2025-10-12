package com.carolinacomba.marketplace.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreatePreferenceRequest {
    
    @NotEmpty(message = "Los items son requeridos")
    @Valid
    private List<ItemRequest> items;
    
    private String externalReference;
    
    private String notificationUrl;
    
    private String successUrl;
    
    private String failureUrl;
    
    private String pendingUrl;
    
    private Boolean autoReturn;
    
    private Integer maxInstallments;
    
    private Integer defaultInstallments;
    
    private String expirationDateFrom;
    
    private String expirationDateTo;
}
