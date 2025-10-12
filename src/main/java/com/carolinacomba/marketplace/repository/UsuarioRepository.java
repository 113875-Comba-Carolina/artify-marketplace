package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.model.Usuario.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    // Consulta SQL nativa para obtener solo los campos del emprendimiento
    @Query(value = "SELECT nombre_emprendimiento, descripcion, ubicacion FROM usuarios WHERE email = :email", nativeQuery = true)
    List<Object[]> findEmprendimientoFieldsByEmail(@Param("email") String email);
    
    // Contar usuarios por rol
    long countByRol(Rol rol);
} 