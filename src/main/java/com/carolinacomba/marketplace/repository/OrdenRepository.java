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
}
