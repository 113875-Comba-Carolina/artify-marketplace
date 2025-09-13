package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.Artesano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtesanoRepository extends JpaRepository<Artesano, Long> {
    
    Optional<Artesano> findByEmail(String email);
    
    List<Artesano> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    List<Artesano> findByNombreEmprendimientoContainingIgnoreCase(String nombreEmprendimiento);
    
    @Query("SELECT a FROM Artesano a WHERE a.nombreEmprendimiento LIKE %:busqueda% OR a.descripcion LIKE %:busqueda%")
    List<Artesano> buscarPorTexto(@Param("busqueda") String busqueda);
} 