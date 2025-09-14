package com.carolinacomba.marketplace.controller.auth;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final IUsuarioService usuarioService;

    @PostMapping("/registro/usuario")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuarioId", usuario.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/registro/artesano")
    public ResponseEntity<?> registrarArtesano(@Valid @RequestBody RegistroArtesanoRequest request) {
        try {
            Artesano artesano = usuarioService.registrarArtesano(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Artesano registrado exitosamente");
            response.put("artesanoId", artesano.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nombre", usuario.getNombre());
            response.put("email", usuario.getEmail());
            response.put("rol", usuario.getRol());
            
            // Si es artesano, incluir datos adicionales
            if (usuario instanceof Artesano) {
                Artesano artesano = (Artesano) usuario;
                response.put("nombreEmprendimiento", artesano.getNombreEmprendimiento());
                response.put("descripcion", artesano.getDescripcion());
                response.put("ubicacion", artesano.getUbicacion());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener perfil: " + e.getMessage());
        }
    }

    @PostMapping("/cambiar-rol")
    public ResponseEntity<?> cambiarRol(@Valid @RequestBody CambioRolRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario usuario = usuarioService.cambiarRol(email, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol cambiado exitosamente");
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
