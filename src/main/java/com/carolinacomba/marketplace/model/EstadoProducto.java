package com.carolinacomba.marketplace.model;

public enum EstadoProducto {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    SIN_STOCK("Sin Stock");
    
    private final String descripcion;
    
    EstadoProducto(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Determina el estado del producto basado en esActivo y stock
     */
    public static EstadoProducto determinarEstado(Boolean esActivo, Integer stock) {
        if (!esActivo) {
            return INACTIVO;
        }
        if (stock == null || stock <= 0) {
            return SIN_STOCK;
        }
        return ACTIVO;
    }
}
