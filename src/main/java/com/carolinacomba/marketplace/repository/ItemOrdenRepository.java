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
           "u.email, " +
           "p.imagen_url " +
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
           "u.email, " +
           "p.imagen_url " +
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
    
    /**
     * Obtiene estadísticas generales de ventas
     */
    @Query(value = "SELECT " +
           "COALESCE(SUM(io.subtotal), 0), " +
           "COALESCE(SUM(io.cantidad), 0), " +
           "COALESCE(AVG(io.subtotal), 0), " +
           "COALESCE(AVG(o.total), 0) " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "WHERE o.estado = 'PAGADO'", nativeQuery = true)
    Object[] findEstadisticasGenerales();
    
    /**
     * Obtiene top artesanos por ventas
     */
    @Query(value = "SELECT " +
           "u.id, " +
           "u.nombre, " +
           "u.email, " +
           "u.nombre_emprendimiento, " +
           "COUNT(DISTINCT o.id) as total_ventas, " +
           "COALESCE(SUM(io.subtotal), 0) as ingresos, " +
           "COUNT(DISTINCT p.id) as total_productos " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN usuarios u ON p.usuario_id = u.id " +
           "WHERE o.estado = 'PAGADO' " +
           "GROUP BY u.id, u.nombre, u.email, u.nombre_emprendimiento " +
           "ORDER BY ingresos DESC " +
           "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopArtesanos();
    
    /**
     * Obtiene productos más vendidos
     */
    @Query(value = "SELECT " +
           "p.id, " +
           "p.nombre, " +
           "p.categoria, " +
           "p.imagen_url, " +
           "u.nombre as artesano_nombre, " +
           "SUM(io.cantidad) as cantidad_vendida, " +
           "COALESCE(SUM(io.subtotal), 0) as ingresos " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN usuarios u ON p.usuario_id = u.id " +
           "WHERE o.estado = 'PAGADO' " +
           "GROUP BY p.id, p.nombre, p.categoria, p.imagen_url, u.nombre " +
           "ORDER BY cantidad_vendida DESC " +
           "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopProductos();
    
    /**
     * Obtiene ventas por categoría
     */
    @Query(value = "SELECT " +
           "p.categoria, " +
           "COUNT(DISTINCT o.id) as total_ventas, " +
           "COALESCE(SUM(io.subtotal), 0) as ingresos, " +
           "COUNT(DISTINCT p.id) as total_productos " +
           "FROM items_orden io " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "WHERE o.estado = 'PAGADO' " +
           "GROUP BY p.categoria " +
           "ORDER BY ingresos DESC", nativeQuery = true)
    List<Object[]> findVentasPorCategoria();
}
