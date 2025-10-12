package com.carolinacomba.marketplace.dto;

import com.carolinacomba.marketplace.model.EstadoOrden;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrdenResponse {
    
    private Long id;
    private String mercadoPagoId;
    private String externalReference;
    private EstadoOrden estado;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String nombreUsuario;
    private String emailUsuario;
    private List<ItemOrdenResponse> items;
}
