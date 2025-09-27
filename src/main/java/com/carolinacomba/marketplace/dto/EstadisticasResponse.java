package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasResponse {
    private long totalProductos;
    private long productosActivos;
    private long productosInactivos;
}
