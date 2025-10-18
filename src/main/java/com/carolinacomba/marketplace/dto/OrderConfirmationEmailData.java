package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmationEmailData {
    private String nombreCliente;
    private String email;
    private Long numeroOrden;
    private LocalDateTime fechaOrden;
    private BigDecimal total;
    private List<OrderItemData> items;
    private String direccionEnvio;
    private String estadoOrden;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemData {
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precio;
        private String nombreArtesano;
    }
}
