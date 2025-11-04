package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarritoResponse {
    
    private Long id;
    private ProductoCarritoDTO producto;
    private Integer cantidad;
    private BigDecimal subtotal;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoCarritoDTO {
        private Long id;
        private String nombre;
        private BigDecimal precio;
        private String imagenUrl;
        private String categoria;
        private Integer stockDisponible;
    }
}

