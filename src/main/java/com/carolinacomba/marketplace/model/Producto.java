package com.carolinacomba.marketplace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    
    @NotNull(message = "La categoría es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProducto categoria;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;
    
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    @Column(length = 500)
    private String imagenUrl;
    
    @Column(name = "es_activo", nullable = false)
    private Boolean esActivo = true;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación con el artesano que creó el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artesano_id", nullable = false)
    private Artesano artesano;
    
    // Constructor personalizado para inicializar fecha de creación
    public Producto(String nombre, String descripcion, BigDecimal precio, CategoriaProducto categoria, 
                    Integer stock, String imagenUrl, Artesano artesano) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.artesano = artesano;
        this.esActivo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Métodos de callback para fechas
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de negocio para manejo de estados
    
    /**
     * Determina el estado actual del producto
     */
    public EstadoProducto getEstado() {
        return EstadoProducto.determinarEstado(this.esActivo, this.stock);
    }
    
    /**
     * Verifica si el producto está disponible para compra
     */
    public boolean isDisponibleParaCompra() {
        return this.esActivo && this.stock != null && this.stock > 0;
    }
    
    /**
     * Verifica si el producto está sin stock
     */
    public boolean isSinStock() {
        return this.stock == null || this.stock <= 0;
    }
    
    /**
     * Verifica si el producto está inactivo
     */
    public boolean isInactivo() {
        return !this.esActivo;
    }
    
    /**
     * Desactiva el producto (soft delete)
     */
    public void desactivar() {
        this.esActivo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Activa el producto
     */
    public void activar() {
        this.esActivo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Reduce el stock del producto
     * @param cantidad La cantidad a reducir
     * @return true si se pudo reducir, false si no hay suficiente stock
     */
    public boolean reducirStock(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            return false;
        }
        if (this.stock == null || this.stock < cantidad) {
            return false;
        }
        this.stock -= cantidad;
        this.fechaActualizacion = LocalDateTime.now();
        return true;
    }
    
    /**
     * Aumenta el stock del producto
     * @param cantidad La cantidad a aumentar
     */
    public void aumentarStock(Integer cantidad) {
        if (cantidad != null && cantidad > 0) {
            this.stock = (this.stock == null ? 0 : this.stock) + cantidad;
            this.fechaActualizacion = LocalDateTime.now();
        }
    }
    
}

