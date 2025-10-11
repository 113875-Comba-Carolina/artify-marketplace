package com.carolinacomba.marketplace.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    
    private String id;
    private String status;
    private String statusDetail;
    private String initPoint;
    private String sandboxInitPoint;
    private String preferenceId;
    private String message;
    private boolean success;
    
    // URLs de retorno
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
}
