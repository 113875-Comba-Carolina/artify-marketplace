package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponse {
    
    private Long id;
    private Long usuarioId;
    private List<ItemCarritoResponse> items;
    private Integer cantidadTotal;
    private BigDecimal totalCarrito;
}

