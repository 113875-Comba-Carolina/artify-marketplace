package com.carolinacomba.marketplace.model;

public enum EstadoOrden {
    PENDIENTE,      // Orden creada, esperando pago
    PAGADO,         // Pago aprobado
    ENVIADO,        // Producto enviado
    ENTREGADO,      // Producto entregado
    CANCELADO,      // Orden cancelada
    REEMBOLSADO     // Pago reembolsado
}
