package com.carolinacomba.marketplace.repository;

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
}
