package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    
    /**
     * Encuentra todos los items de un carrito
     */
    List<ItemCarrito> findByCarritoId(Long carritoId);
    
    /**
     * Encuentra un item específico en un carrito
     */
    @Query("SELECT ic FROM ItemCarrito ic WHERE ic.carrito.id = :carritoId AND ic.producto.id = :productoId")
    Optional<ItemCarrito> findByCarritoIdAndProductoId(@Param("carritoId") Long carritoId, @Param("productoId") Long productoId);
    
    /**
     * Elimina todos los items de un carrito
     */
    @Modifying
    @Query("DELETE FROM ItemCarrito ic WHERE ic.carrito.id = :carritoId")
    void deleteByCarritoId(@Param("carritoId") Long carritoId);
    
    /**
     * Elimina un item específico de un carrito
     */
    @Modifying
    @Query("DELETE FROM ItemCarrito ic WHERE ic.carrito.id = :carritoId AND ic.producto.id = :productoId")
    void deleteByCarritoIdAndProductoId(@Param("carritoId") Long carritoId, @Param("productoId") Long productoId);
}

