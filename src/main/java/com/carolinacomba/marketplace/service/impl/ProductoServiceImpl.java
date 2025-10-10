package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.ProductoRequest;
import com.carolinacomba.marketplace.dto.ProductoResponse;
import com.carolinacomba.marketplace.dto.ProductoWithImageRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.model.Producto;
import com.carolinacomba.marketplace.repository.ProductoRepository;
import com.carolinacomba.marketplace.service.IProductoService;
import com.carolinacomba.marketplace.service.ImageUploadService;
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
    
    @Autowired
    private ImageUploadService imageUploadService;
    
    @Override
    public ProductoResponse crearProducto(ProductoRequest productoRequest, Artesano artesano) {
        System.out.println("=== DEBUG CREAR PRODUCTO ===");
        System.out.println("Artesano ID: " + artesano.getId());
        System.out.println("Artesano Email: " + artesano.getEmail());
        
        Producto producto = new Producto();
        producto.setNombre(productoRequest.getNombre());
        producto.setDescripcion(productoRequest.getDescripcion());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setCategoria(productoRequest.getCategoria());
        producto.setStock(productoRequest.getStock());
        producto.setImagenUrl(productoRequest.getImagenUrl());
        producto.setUsuario(artesano);
        producto.setEsActivo(true);
        
        System.out.println("Guardando producto...");
        Producto productoGuardado = productoRepository.save(producto);
        System.out.println("Producto guardado con ID: " + productoGuardado.getId());
        return new ProductoResponse(productoGuardado);
    }
    
    @Override
    public ProductoResponse crearProductoConImagen(ProductoWithImageRequest productoRequest, Artesano artesano) {
        String imagenUrl = null;
        
        // Si se proporcionó una imagen, subirla a ImgBB
        if (productoRequest.hasImage()) {
            try {
                imagenUrl = imageUploadService.uploadImage(productoRequest.getImagen());
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen: " + e.getMessage(), e);
            }
        } else if (productoRequest.hasImageUrl()) {
            // Si se proporcionó una URL de imagen existente, usarla
            imagenUrl = productoRequest.getImagenUrl();
        }
        
        // Crear el ProductoRequest estándar
        ProductoRequest request = productoRequest.toProductoRequest();
        request.setImagenUrl(imagenUrl);
        
        // Usar el método existente para crear el producto
        return crearProducto(request, artesano);
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
        List<Producto> productos = productoRepository.findByUsuarioAndEsActivoTrue(artesano);
        return productos.stream()
            .map(ProductoResponse::new)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosPorArtesano(Artesano artesano, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByUsuario(artesano, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosActivosPorArtesano(Artesano artesano, Pageable pageable) {
        System.out.println("=== DEBUG OBTENER PRODUCTOS ACTIVOS ===");
        System.out.println("Artesano ID: " + artesano.getId());
        System.out.println("Artesano Email: " + artesano.getEmail());
        
        // Buscar por ID del usuario en lugar del objeto completo
        Page<Producto> productos = productoRepository.findByUsuarioIdAndEsActivoTrue(artesano.getId(), pageable);
        System.out.println("Productos encontrados: " + productos.getTotalElements());
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
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
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
    public ProductoResponse actualizarProductoConImagen(Long id, ProductoWithImageRequest productoRequest, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para actualizar este producto");
        }
        
        String imagenUrl = producto.getImagenUrl(); // Mantener la imagen actual por defecto
        
        // Si se proporcionó una nueva imagen, subirla a ImgBB
        if (productoRequest.hasImage()) {
            try {
                imagenUrl = imageUploadService.uploadImage(productoRequest.getImagen());
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen: " + e.getMessage(), e);
            }
        } else if (productoRequest.hasImageUrl()) {
            // Si se proporcionó una URL de imagen existente, usarla
            imagenUrl = productoRequest.getImagenUrl();
        }
        
        // Actualizar los campos del producto
        producto.setNombre(productoRequest.getNombre());
        producto.setDescripcion(productoRequest.getDescripcion());
        producto.setPrecio(productoRequest.getPrecio());
        producto.setCategoria(productoRequest.getCategoria());
        producto.setStock(productoRequest.getStock());
        producto.setImagenUrl(imagenUrl);
        
        Producto productoActualizado = productoRepository.save(producto);
        return new ProductoResponse(productoActualizado);
    }
    
    @Override
    public void eliminarProducto(Long id, Artesano artesano) {
        // Este método ahora llama a desactivarProducto para mantener consistencia
        desactivarProducto(id, artesano);
    }
    
    @Override
    public ProductoResponse desactivarProducto(Long id, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para desactivar este producto");
        }
        
        // Desactivar el producto
        producto.desactivar();
        Producto productoActualizado = productoRepository.save(producto);
        return new ProductoResponse(productoActualizado);
    }
    
    @Override
    public ProductoResponse activarProducto(Long id, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para activar este producto");
        }
        
        // Activar el producto
        producto.activar();
        Producto productoActualizado = productoRepository.save(producto);
        return new ProductoResponse(productoActualizado);
    }
    
    @Override
    public void eliminarProductoDefinitivamente(Long id, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar este producto");
        }
        
        // Solo permitir eliminación definitiva si está inactivo
        if (producto.getEsActivo()) {
            throw new RuntimeException("No se puede eliminar definitivamente un producto activo. Primero debe desactivarlo.");
        }
        
        // Eliminación definitiva
        productoRepository.delete(producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosInactivos(Artesano artesano, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByUsuarioAndEsActivoFalse(artesano, pageable);
        return productos.map(ProductoResponse::new);
    }
    
    @Override
    public ProductoResponse cambiarEstadoProducto(Long id, Boolean esActivo, Artesano artesano) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Verificar que el producto pertenece al artesano
        if (!producto.getUsuario().getId().equals(artesano.getId())) {
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
        return productoRepository.existsByIdAndUsuario(productoId, artesano);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarProductosPorArtesano(Artesano artesano) {
        System.out.println("=== DEBUG CONTAR PRODUCTOS ===");
        System.out.println("Artesano ID: " + artesano.getId());
        return productoRepository.countByUsuarioId(artesano.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarProductosActivosPorArtesano(Artesano artesano) {
        System.out.println("=== DEBUG CONTAR PRODUCTOS ACTIVOS ===");
        System.out.println("Artesano ID: " + artesano.getId());
        return productoRepository.countByUsuarioIdAndEsActivoTrue(artesano.getId());
    }
}

