package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.Carrito;
import com.carolinacomba.marketplace.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    /**
     * Busca el carrito de un usuario específico
     */
    Optional<Carrito> findByUsuario(Usuario usuario);
    
    /**
     * Busca el carrito por ID de usuario
     */
    Optional<Carrito> findByUsuarioId(Long usuarioId);
    
    /**
     * Verifica si un usuario ya tiene un carrito
     */
    boolean existsByUsuarioId(Long usuarioId);
}

