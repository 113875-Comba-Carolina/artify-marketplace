package com.carolinacomba.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemRequest {
    
    @NotBlank(message = "El título es requerido")
    private String title;
    
    private String description;
    
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer quantity;
    
    @NotNull(message = "El precio unitario es requerido")
    @Positive(message = "El precio unitario debe ser positivo")
    private Double unitPrice;
    
    private String pictureUrl;
    
    private String categoryId;
}
