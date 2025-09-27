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
    
}

