package com.carolinacomba.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOrdenRepository extends JpaRepository<com.carolinacomba.marketplace.model.ItemOrden, Long> {
    
    /**
     * Obtiene todas las ventas de un artesano específico
     * @param artesanoId ID del artesano
     * @return Lista de arrays de objetos con información de ventas
     */
    @Query(value = "SELECT " +
           "o.id, " +
           "p.id, " +
           "p.nombre, " +
           "io.cantidad, " +
           "io.precio_unitario, " +
           "io.subtotal, " +
           "o.estado, " +
           "o.fecha_creacion, " +
           "u.nombre, " +
           "u.email " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN usuarios u ON o.usuario_id = u.id " +
           "WHERE p.usuario_id = :artesanoId " +
           "ORDER BY o.fecha_creacion DESC", nativeQuery = true)
    List<Object[]> findVentasPorArtesano(@Param("artesanoId") Long artesanoId);
    
    /**
     * Obtiene las ventas de un artesano filtradas por estado de orden
     * @param artesanoId ID del artesano
     * @param estado Estado de la orden
     * @return Lista de arrays de objetos con información de ventas
     */
    @Query(value = "SELECT " +
           "o.id, " +
           "p.id, " +
           "p.nombre, " +
           "io.cantidad, " +
           "io.precio_unitario, " +
           "io.subtotal, " +
           "o.estado, " +
           "o.fecha_creacion, " +
           "u.nombre, " +
           "u.email " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN usuarios u ON o.usuario_id = u.id " +
           "WHERE p.usuario_id = :artesanoId " +
           "AND o.estado = :estado " +
           "ORDER BY o.fecha_creacion DESC", nativeQuery = true)
    List<Object[]> findVentasPorArtesanoYEstado(@Param("artesanoId") Long artesanoId, @Param("estado") String estado);
    
    /**
     * Obtiene estadísticas de ventas de un artesano
     * @param artesanoId ID del artesano
     * @return Array con estadísticas: [totalVentas, totalIngresos, totalProductosVendidos, ordenesPagadas]
     */
    @Query(value = "SELECT " +
           "COUNT(DISTINCT o.id), " +
           "COALESCE(SUM(io.subtotal), 0), " +
           "COALESCE(SUM(io.cantidad), 0), " +
           "COUNT(DISTINCT CASE WHEN o.estado = 'PAGADO' THEN o.id END) " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "WHERE p.usuario_id = :artesanoId", nativeQuery = true)
    Object[] findEstadisticasVentasPorArtesano(@Param("artesanoId") Long artesanoId);
}
