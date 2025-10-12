package com.carolinacomba.marketplace.dto;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@Builder
public class CarritoItem {
    
    private Long productoId;
    private String nombreProducto;
    private String imagenUrl;
    private String categoria;
    private Integer cantidad;
    private BigDecimal precio;
}
