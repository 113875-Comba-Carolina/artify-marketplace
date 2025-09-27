package com.carolinacomba.marketplace.repository;

import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos por artesano
    List<Producto> findByArtesano(Artesano artesano);
    
    // Buscar productos por artesano con paginación
    Page<Producto> findByArtesano(Artesano artesano, Pageable pageable);
    
    // Buscar productos activos por artesano
    List<Producto> findByArtesanoAndEsActivoTrue(Artesano artesano);
    
    // Buscar productos activos por artesano con paginación
    Page<Producto> findByArtesanoAndEsActivoTrue(Artesano artesano, Pageable pageable);
    
    // Buscar productos por categoría
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    // Buscar productos activos por categoría
    List<Producto> findByCategoriaAndEsActivoTrue(CategoriaProducto categoria);
    
    // Buscar productos activos por categoría con paginación
    Page<Producto> findByCategoriaAndEsActivoTrue(CategoriaProducto categoria, Pageable pageable);
    
    // Buscar todos los productos activos
    List<Producto> findByEsActivoTrue();
    
    // Buscar todos los productos activos con paginación
    Page<Producto> findByEsActivoTrue(Pageable pageable);
    
    // Buscar productos por nombre (búsqueda parcial)
    List<Producto> findByNombreContainingIgnoreCaseAndEsActivoTrue(String nombre);
    
    // Buscar productos por nombre con paginación
    Page<Producto> findByNombreContainingIgnoreCaseAndEsActivoTrue(String nombre, Pageable pageable);
    
    // Buscar productos por rango de precios
    List<Producto> findByPrecioBetweenAndEsActivoTrue(java.math.BigDecimal precioMin, java.math.BigDecimal precioMax);
    
    // Buscar productos por rango de precios con paginación
    Page<Producto> findByPrecioBetweenAndEsActivoTrue(java.math.BigDecimal precioMin, java.math.BigDecimal precioMax, Pageable pageable);
    
    // Contar productos por artesano
    long countByArtesano(Artesano artesano);
    
    // Contar productos activos por artesano
    long countByArtesanoAndEsActivoTrue(Artesano artesano);
    
    // Verificar si un producto pertenece a un artesano específico
    boolean existsByIdAndArtesano(Long productoId, Artesano artesano);
    
    // Buscar productos con stock disponible
    List<Producto> findByStockGreaterThanAndEsActivoTrue(Integer stock);
    
    // Buscar productos con stock disponible con paginación
    Page<Producto> findByStockGreaterThanAndEsActivoTrue(Integer stock, Pageable pageable);
    
    // Búsqueda avanzada con múltiples criterios
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
           "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
           "p.esActivo = true")
    Page<Producto> buscarProductosAvanzada(
        @Param("nombre") String nombre,
        @Param("categoria") CategoriaProducto categoria,
        @Param("precioMin") java.math.BigDecimal precioMin,
        @Param("precioMax") java.math.BigDecimal precioMax,
        Pageable pageable
    );
    
    // Obtener productos más recientes
    @Query("SELECT p FROM Producto p WHERE p.esActivo = true ORDER BY p.fechaCreacion DESC")
    List<Producto> findProductosRecientes(Pageable pageable);
    
    // Obtener productos por artesano ordenados por fecha de creación
    @Query("SELECT p FROM Producto p WHERE p.artesano = :artesano ORDER BY p.fechaCreacion DESC")
    List<Producto> findByArtesanoOrderByFechaCreacionDesc(Artesano artesano);
}

