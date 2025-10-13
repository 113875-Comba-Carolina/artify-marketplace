package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.ProductoRequest;
import com.carolinacomba.marketplace.dto.ProductoResponse;
import com.carolinacomba.marketplace.dto.ProductoWithImageRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {
    
    // Crear un nuevo producto
    ProductoResponse crearProducto(ProductoRequest productoRequest, Artesano artesano);
    
    // Crear un nuevo producto con imagen
    ProductoResponse crearProductoConImagen(ProductoWithImageRequest productoRequest, Artesano artesano);
    
    // Obtener un producto por ID
    ProductoResponse obtenerProductoPorId(Long id);
    
    // Obtener todos los productos activos
    List<ProductoResponse> obtenerTodosLosProductos();
    
    // Obtener todos los productos activos sin paginación (para frontend)
    List<ProductoResponse> obtenerTodosLosProductosSinPaginacion();
    
    // Obtener todos los productos activos con paginación
    Page<ProductoResponse> obtenerTodosLosProductos(Pageable pageable);
    
    // Obtener productos por artesano
    List<ProductoResponse> obtenerProductosPorArtesano(Artesano artesano);
    
    // Obtener productos por artesano con paginación
    Page<ProductoResponse> obtenerProductosPorArtesano(Artesano artesano, Pageable pageable);
    
    // Obtener productos activos por artesano con paginación
    Page<ProductoResponse> obtenerProductosActivosPorArtesano(Artesano artesano, Pageable pageable);
    
    // Obtener productos por categoría
    List<ProductoResponse> obtenerProductosPorCategoria(CategoriaProducto categoria);
    
    // Obtener productos por categoría con paginación
    Page<ProductoResponse> obtenerProductosPorCategoria(CategoriaProducto categoria, Pageable pageable);
    
    // Buscar productos por nombre
    List<ProductoResponse> buscarProductosPorNombre(String nombre);
    
    // Buscar productos por nombre con paginación
    Page<ProductoResponse> buscarProductosPorNombre(String nombre, Pageable pageable);
    
    // Búsqueda avanzada
    Page<ProductoResponse> buscarProductosAvanzada(String nombre, CategoriaProducto categoria, 
                                                  BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Actualizar un producto
    ProductoResponse actualizarProducto(Long id, ProductoRequest productoRequest, Artesano artesano);
    
    // Actualizar un producto con imagen
    ProductoResponse actualizarProductoConImagen(Long id, ProductoWithImageRequest productoRequest, Artesano artesano);
    
    // Eliminar un producto (soft delete)
    void eliminarProducto(Long id, Artesano artesano);
    
    // Activar/Desactivar producto
    ProductoResponse cambiarEstadoProducto(Long id, Boolean esActivo, Artesano artesano);
    
    // Desactivar producto (soft delete)
    ProductoResponse desactivarProducto(Long id, Artesano artesano);
    
    // Activar producto
    ProductoResponse activarProducto(Long id, Artesano artesano);
    
    // Eliminar producto definitivamente (solo si está inactivo)
    void eliminarProductoDefinitivamente(Long id, Artesano artesano);
    
    // Obtener productos inactivos del artesano
    Page<ProductoResponse> obtenerProductosInactivos(Artesano artesano, Pageable pageable);
    
    // Obtener productos con stock disponible
    List<ProductoResponse> obtenerProductosConStock();
    
    // Obtener productos con stock disponible con paginación
    Page<ProductoResponse> obtenerProductosConStock(Pageable pageable);
    
    // Obtener productos más recientes
    List<ProductoResponse> obtenerProductosRecientes(Pageable pageable);
    
    // Verificar si un producto pertenece a un artesano
    boolean productoPerteneceAArtesano(Long productoId, Artesano artesano);
    
    // Contar productos por artesano
    long contarProductosPorArtesano(Artesano artesano);
    
    // Contar productos activos por artesano
    long contarProductosActivosPorArtesano(Artesano artesano);
}

