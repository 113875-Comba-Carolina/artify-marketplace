package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.CarritoResponse;
import com.carolinacomba.marketplace.dto.ItemCarritoRequest;
import com.carolinacomba.marketplace.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CarritoController {
    
    private final CarritoService carritoService;
    
    /**
     * Obtiene el carrito del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito() {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.obtenerCarrito(email);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Agrega un producto al carrito
     */
    @PostMapping("/agregar")
    public ResponseEntity<CarritoResponse> agregarAlCarrito(@Valid @RequestBody ItemCarritoRequest request) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.agregarAlCarrito(email, request);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.actualizarCantidad(email, productoId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Elimina un producto del carrito
     */
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarDelCarrito(@PathVariable Long productoId) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.eliminarDelCarrito(email, productoId);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Limpia todo el carrito
     */
    @DeleteMapping("/limpiar")
    public ResponseEntity<CarritoResponse> limpiarCarrito() {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.limpiarCarrito(email);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Sincroniza el carrito local con el del servidor
     * Útil cuando un usuario inicia sesión con items en su carrito local
     */
    @PostMapping("/sincronizar")
    public ResponseEntity<CarritoResponse> sincronizarCarrito(@RequestBody List<ItemCarritoRequest> itemsLocales) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            CarritoResponse carrito = carritoService.sincronizarCarrito(email, itemsLocales);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtiene el email del usuario autenticado
     */
    private String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return authentication.getName();
    }
}

