package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.EstadisticasResponse;
import com.carolinacomba.marketplace.dto.ProductoRequest;
import com.carolinacomba.marketplace.dto.ProductoResponse;
import com.carolinacomba.marketplace.dto.ProductoWithImageRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.CategoriaProducto;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.service.IProductoService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import com.carolinacomba.marketplace.service.ImageUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ProductoController {
    
    private final IProductoService productoService;
    private final ArtesanoRepository artesanoRepository;
    private final IUsuarioService usuarioService;
    private final ImageUploadService imageUploadService;
    
    // ENDPOINTS PÚBLICOS
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> obtenerProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos(pageable));
    }
    
    @GetMapping("/todos")
    public ResponseEntity<List<ProductoResponse>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductosSinPaginacion());
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
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.buscarProductosPorNombre(nombre, pageable));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<ProductoResponse>> productosPorCategoria(
            @PathVariable CategoriaProducto categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.obtenerProductosPorCategoria(categoria, pageable));
    }
    
    @GetMapping("/buscar-avanzada")
    public ResponseEntity<Page<ProductoResponse>> buscarProductosAvanzada(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) CategoriaProducto categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productoService.buscarProductosAvanzada(nombre, categoria, precioMin, precioMax, pageable));
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
    
    @PostMapping(value = "/con-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponse> crearProductoConImagen(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("categoria") CategoriaProducto categoria,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            
            // Crear el DTO con imagen
            ProductoWithImageRequest request = new ProductoWithImageRequest();
            request.setNombre(nombre);
            request.setDescripcion(descripcion);
            request.setPrecio(precio);
            request.setCategoria(categoria);
            request.setStock(stock);
            request.setImagen(imagen);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProductoConImagen(request, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping(value = "/{id}/con-imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponse> actualizarProductoConImagen(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("categoria") CategoriaProducto categoria,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "imagenUrl", required = false) String imagenUrl) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            
            // Crear el DTO con imagen
            ProductoWithImageRequest request = new ProductoWithImageRequest();
            request.setNombre(nombre);
            request.setDescripcion(descripcion);
            request.setPrecio(precio);
            request.setCategoria(categoria);
            request.setStock(stock);
            request.setImagen(imagen);
            request.setImagenUrl(imagenUrl);
            
            return ResponseEntity.ok(productoService.actualizarProductoConImagen(id, request, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            @RequestParam(defaultValue = "15") int size) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(productoService.obtenerProductosPorArtesano(artesano, pageable));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/mis-productos-activos")
    public ResponseEntity<Page<ProductoResponse>> misProductosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(productoService.obtenerProductosActivosPorArtesano(artesano, pageable));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/inactivos")
    public ResponseEntity<Page<ProductoResponse>> misProductosInactivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(productoService.obtenerProductosInactivos(artesano, pageable));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponse> desactivarProducto(@PathVariable Long id) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            return ResponseEntity.ok(productoService.desactivarProducto(id, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/activar")
    public ResponseEntity<ProductoResponse> activarProducto(@PathVariable Long id) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            return ResponseEntity.ok(productoService.activarProducto(id, artesano));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/definitivo")
    public ResponseEntity<Void> eliminarProductoDefinitivamente(@PathVariable Long id) {
        try {
            Artesano artesano = obtenerArtesanoAutenticado();
            productoService.eliminarProductoDefinitivamente(id, artesano);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private Artesano obtenerArtesanoAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        

        return artesanoRepository.findByEmail(email)
            .orElseGet(() -> {
                Usuario usuario = usuarioService.findByEmail(email);
                
                if (usuario != null && usuario.getRol() == Usuario.Rol.ARTESANO) {
                    Artesano artesano = new Artesano();
                    artesano.setId(usuario.getId());
                    artesano.setNombre(usuario.getNombre());
                    artesano.setEmail(usuario.getEmail());
                    artesano.setContrasena(usuario.getContrasena());
                    artesano.setRol(usuario.getRol());
                    artesano.setTelefono(usuario.getTelefono());
                    
                    List<Object[]> emprendimientoFields = usuarioService.findEmprendimientoFieldsByEmail(email);
                    if (emprendimientoFields != null && !emprendimientoFields.isEmpty()) {
                        Object[] fields = emprendimientoFields.get(0);
                        if (fields.length >= 3) {
                            artesano.setNombreEmprendimiento((String) fields[0]);
                            artesano.setDescripcion((String) fields[1]);
                            artesano.setUbicacion((String) fields[2]);
                        }
                    }
                    
                    return artesano;
                }
                throw new RuntimeException("Artesano no encontrado");
            });
    }
}
