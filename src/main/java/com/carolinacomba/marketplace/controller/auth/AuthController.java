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
import java.util.List;
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
            if (usuario.getRol() == Usuario.Rol.ARTESANO) {
                // Obtener datos del emprendimiento desde la base de datos
                List<Object[]> emprendimientoFields = usuarioService.findEmprendimientoFieldsByEmail(email);
                if (emprendimientoFields != null && !emprendimientoFields.isEmpty()) {
                    Object[] fields = emprendimientoFields.get(0);
                    if (fields.length >= 3) {
                        response.put("nombreEmprendimiento", fields[0]);
                        response.put("descripcion", fields[1]);
                        response.put("ubicacion", fields[2]);
                    }
                }
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            usuarioService.createPasswordResetToken(request.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Se ha enviado un correo con instrucciones para restablecer tu contraseña");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            usuarioService.resetPassword(request.getToken(), request.getNewPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tu contraseña ha sido restablecida exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
