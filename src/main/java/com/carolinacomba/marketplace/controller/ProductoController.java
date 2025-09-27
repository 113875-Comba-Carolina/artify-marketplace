package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.EstadisticasResponse;
import com.carolinacomba.marketplace.dto.ProductoRequest;
import com.carolinacomba.marketplace.dto.ProductoResponse;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.service.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProductoController {
    
    private final IProductoService productoService;
    private final ArtesanoRepository artesanoRepository;
    
    // ENDPOINTS PÚBLICOS
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> obtenerProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerProducto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoResponse>> buscarProductos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.buscarProductosPorNombre(nombre, pageable));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<ProductoResponse>> productosPorCategoria(
            @PathVariable CategoriaProducto categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.obtenerProductosPorCategoria(categoria, pageable));
    }
    
    // ENDPOINTS PRIVADOS (Solo artesanos y admin)
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(request, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            return ResponseEntity.ok(productoService.actualizarProducto(id, request, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            productoService.eliminarProducto(id, artesano);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/mis-productos")
    public ResponseEntity<Page<ProductoResponse>> misProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(productoService.obtenerProductosPorArtesano(artesano, pageable));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/mis-estadisticas")
    public ResponseEntity<EstadisticasResponse> misEstadisticas() {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            long total = productoService.contarProductosPorArtesano(artesano);
            long activos = productoService.contarProductosActivosPorArtesano(artesano);
            return ResponseEntity.ok(new EstadisticasResponse(total, activos, total - activos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    // Método auxiliar para obtener el artesano autenticado
    private Artesano obtenerArtesanoAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return artesanoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Artesano no encontrado"));
    }
}
