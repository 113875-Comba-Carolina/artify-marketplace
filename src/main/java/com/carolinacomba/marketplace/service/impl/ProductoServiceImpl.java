package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.ProductoRequest;
import com.carolinacomba.marketplace.dto.ProductoResponse;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.model.Producto;
import com.carolinacomba.marketplace.repository.ProductoRepository;
import com.carolinacomba.marketplace.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Override
    public ProductoResponse crearProducto(ProductoRequest productoRequest, Artesano artesano) {
        Producto producto = new Producto();
        producto.setNombre(productoRequest.getNombre());
        producto.setDescripcion(productoRequest.getDescripcion());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setCategoria(productoRequest.getCategoria());
        producto.setStock(productoRequest.getStock());
        producto.setImagenUrl(productoRequest.getImagenUrl());
        producto.setArtesano(artesano);
        producto.setEsActivo(true);
        
        Producto productoGuardado = productoRepository.save(producto);
        return new ProductoResponse(productoGuardado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return new ProductoResponse(producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepository.findByEsActivoTrue();
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerTodosLosProductos(Pageable pageable) {
        Page<Producto> productos = productoRepository.findByEsActivoTrue(pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosPorArtesano(Artesano artesano) {
        List<Producto> productos = productoRepository.findByArtesanoAndEsActivoTrue(artesano);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosPorArtesano(Artesano artesano, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByArtesanoAndEsActivoTrue(artesano, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosPorCategoria(CategoriaProducto categoria) {
        List<Producto> productos = productoRepository.findByCategoriaAndEsActivoTrue(categoria);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosPorCategoria(CategoriaProducto categoria, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByCategoriaAndEsActivoTrue(categoria, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarProductosPorNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndEsActivoTrue(nombre);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarProductosPorNombre(String nombre, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndEsActivoTrue(nombre, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarProductosAvanzada(String nombre, CategoriaProducto categoria, 
                                                         BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        Page<Producto> productos = productoRepository.buscarProductosAvanzada(nombre, categoria, precioMin, precioMax, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    public ProductoResponse actualizarProducto(Long id, ProductoRequest productoRequest, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getArtesano().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para actualizar este producto");
        }
        
        producto.setNombre(productoRequest.getNombre());
        producto.setDescripcion(productoRequest.getDescripcion());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setCategoria(productoRequest.getCategoria());
        producto.setStock(productoRequest.getStock());
        producto.setImagenUrl(productoRequest.getImagenUrl());
        
        Producto productoActualizado = productoRepository.save(producto);
        return new ProductoResponse(productoActualizado);
    }
    
    @Override
    public void eliminarProducto(Long id, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getArtesano().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar este producto");
        }
        
        // Soft delete - marcar como inactivo
        producto.setEsActivo(false);
        productoRepository.save(producto);
    }
    
    @Override
    public ProductoResponse cambiarEstadoProducto(Long id, Boolean esActivo, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getArtesano().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para cambiar el estado de este producto");
        }
        
        producto.setEsActivo(esActivo);
        Producto productoActualizado = productoRepository.save(producto);
        return new ProductoResponse(productoActualizado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosConStock() {
        List<Producto> productos = productoRepository.findByStockGreaterThanAndEsActivoTrue(0);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosConStock(Pageable pageable) {
        Page<Producto> productos = productoRepository.findByStockGreaterThanAndEsActivoTrue(0, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosRecientes(Pageable pageable) {
        List<Producto> productos = productoRepository.findProductosRecientes(pageable);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean productoPerteneceAArtesano(Long productoId, Artesano artesano) {
        return productoRepository.existsByIdAndArtesano(productoId, artesano);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarProductosPorArtesano(Artesano artesano) {
        return productoRepository.countByArtesano(artesano);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarProductosActivosPorArtesano(Artesano artesano) {
        return productoRepository.countByArtesanoAndEsActivoTrue(artesano);
    }
}

