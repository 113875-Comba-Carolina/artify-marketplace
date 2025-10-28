package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.EstadoOrden;
import com.carolinacomba.marketplace.model.Orden;
import com.carolinacomba.marketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    Optional<Orden> findByMercadoPagoId(String mercadoPagoId);
    
    Optional<Orden> findByExternalReference(String externalReference);
    
    @Query("SELECT o FROM Orden o WHERE o.usuario = :usuario AND o.estado = :estado ORDER BY o.fechaCreacion DESC")
    List<Orden> findByUsuarioAndEstado(@Param("usuario") Usuario usuario, @Param("estado") String estado);
    
    // Contar órdenes por estado
    long countByEstado(EstadoOrden estado);
    
    // Actividad reciente
    @Query(value = "SELECT o.id, p.nombre, o.total, o.fecha_creacion " +
           "FROM ordenes o " +
           "JOIN items_orden io ON o.id = io.orden_id " +
           "JOIN productos p ON io.producto_id = p.id " +
           "ORDER BY o.fecha_creacion DESC " +
           "LIMIT 10", nativeQuery = true)
    List<Object[]> findActividadReciente();
    
    // Estadísticas del comprador
    @Query(value = "SELECT " +
           "COUNT(DISTINCT o.id) as total_ordenes, " +
           "COALESCE(SUM(CASE WHEN o.estado = 'PAGADO' THEN o.total ELSE 0 END), 0) as total_gastado, " +
           "COALESCE(SUM(CASE WHEN o.estado = 'PAGADO' THEN io.cantidad ELSE 0 END), 0) as total_productos, " +
           "COALESCE(AVG(CASE WHEN o.estado = 'PAGADO' THEN o.total ELSE NULL END), 0) as promedio_por_compra " +
           "FROM ordenes o " +
           "LEFT JOIN items_orden io ON o.id = io.orden_id " +
           "WHERE o.usuario_id = :usuarioId", nativeQuery = true)
    Object[] findEstadisticasComprador(@Param("usuarioId") Long usuarioId);
    
    @Query(value = "SELECT " +
           "p.categoria, " +
           "COUNT(*) as cantidad_comprada, " +
           "SUM(io.subtotal) as total_gastado_categoria " +
           "FROM items_orden io " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "WHERE o.usuario_id = :usuarioId AND o.estado = 'PAGADO' " +
           "GROUP BY p.categoria " +
           "ORDER BY cantidad_comprada DESC", nativeQuery = true)
    List<Object[]> findCategoriasFavoritasPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query(value = "SELECT " +
           "p.nombre, " +
           "p.imagen_url, " +
           "SUM(io.cantidad) as total_comprado, " +
           "SUM(io.subtotal) as total_gastado_producto " +
           "FROM items_orden io " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "WHERE o.usuario_id = :usuarioId AND o.estado = 'PAGADO' " +
           "GROUP BY p.id, p.nombre, p.imagen_url " +
           "ORDER BY total_comprado DESC " +
           "LIMIT 5", nativeQuery = true)
    List<Object[]> findProductosMasCompradosPorUsuario(@Param("usuarioId") Long usuarioId);
    
    @Query(value = "SELECT " +
           "artesano.nombre as artesano_nombre, " +
           "COUNT(DISTINCT o.id) as ordenes_con_artesano, " +
           "SUM(io.subtotal) as total_gastado_artesano " +
           "FROM items_orden io " +
           "JOIN productos p ON io.producto_id = p.id " +
           "JOIN ordenes o ON io.orden_id = o.id " +
           "JOIN usuarios artesano ON p.usuario_id = artesano.id " +
           "WHERE o.usuario_id = :usuarioId AND o.estado = 'PAGADO' " +
           "GROUP BY artesano.id, artesano.nombre " +
           "ORDER BY ordenes_con_artesano DESC " +
           "LIMIT 5", nativeQuery = true)
    List<Object[]> findArtesanosFavoritosPorUsuario(@Param("usuarioId") Long usuarioId);
}
