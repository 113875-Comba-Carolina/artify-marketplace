package com.carolinacomba.marketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Agrega un item al carrito o actualiza la cantidad si ya existe
     */
    public void agregarItem(Producto producto, Integer cantidad) {
        ItemCarrito itemExistente = items.stream()
            .filter(item -> item.getProducto().getId().equals(producto.getId()))
            .findFirst()
            .orElse(null);
        
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(this);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            items.add(nuevoItem);
        }
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Elimina un item del carrito
     */
    public void eliminarItem(Long productoId) {
        items.removeIf(item -> item.getProducto().getId().equals(productoId));
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Actualiza la cantidad de un item
     */
    public void actualizarCantidadItem(Long productoId, Integer nuevaCantidad) {
        ItemCarrito item = items.stream()
            .filter(i -> i.getProducto().getId().equals(productoId))
            .findFirst()
            .orElse(null);
        
        if (item != null) {
            if (nuevaCantidad <= 0) {
                eliminarItem(productoId);
            } else {
                item.setCantidad(nuevaCantidad);
                this.fechaActualizacion = LocalDateTime.now();
            }
        }
    }
    
    /**
     * Limpia todos los items del carrito
     */
    public void limpiar() {
        items.clear();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Obtiene el total de items en el carrito
     */
    public int getCantidadTotal() {
        return items.stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }
}

