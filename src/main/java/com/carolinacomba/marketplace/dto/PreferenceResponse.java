package com.carolinacomba.marketplace.dto;

import lombok.Data;

@Data
public class PreferenceResponse {
    
    private String id;
    private String initPoint;
    private String sandboxInitPoint;
    private String status;
    private String message;
    private boolean success;
}
