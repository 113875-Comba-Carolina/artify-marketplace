package com.carolinacomba.marketplace.dto;

import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.model.EstadoProducto;
import com.carolinacomba.marketplace.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaProducto categoria;
    private Integer stock;
    private String imagenUrl;
    private Boolean esActivo;
    private EstadoProducto estado;
    private Boolean disponibleParaCompra;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private ArtesanoInfo artesano;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtesanoInfo {
        private Long id;
        private String nombre;
        private String email;
        private String nombreEmprendimiento;
        private String descripcion;
        private String ubicacion;
    }
    
    // Constructor personalizado para convertir desde Producto
    public ProductoResponse(Producto producto) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecio();
        this.categoria = producto.getCategoria();
        this.stock = producto.getStock();
        this.imagenUrl = producto.getImagenUrl();
        this.esActivo = producto.getEsActivo();
        this.estado = producto.getEstado();
        this.disponibleParaCompra = producto.isDisponibleParaCompra();
        this.fechaCreacion = producto.getFechaCreacion();
        this.fechaActualizacion = producto.getFechaActualizacion();
        this.artesano = new ArtesanoInfo(
            producto.getUsuario().getId(),
            producto.getUsuario().getNombre(),
            producto.getUsuario().getEmail(),
            producto.getUsuario().getNombreEmprendimiento(),
            producto.getUsuario().getDescripcion(),
            producto.getUsuario().getUbicacion()
        );
    }
    
}

