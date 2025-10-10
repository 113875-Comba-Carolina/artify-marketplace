package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.ArtesanoResponse;
import com.carolinacomba.marketplace.dto.UsuarioResponse;
import com.carolinacomba.marketplace.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "http://localhost:4200")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping
    public ResponseEntity<?> obtenerPerfil(@RequestHeader("Authorization") String authHeader) {
        try {
            String[] credentials = extractCredentials(authHeader);
            String email = credentials[0];
            String password = credentials[1];

            Object perfil = perfilService.obtenerPerfil(email, password);
            return ResponseEntity.ok(perfil);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizarPerfil(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> perfilData) {
        try {
            String[] credentials = extractCredentials(authHeader);
            String email = credentials[0];
            String password = credentials[1];

            Object perfilActualizado = perfilService.actualizarPerfil(email, password, perfilData);
            return ResponseEntity.ok(perfilActualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> passwordData) {
        try {
            String[] credentials = extractCredentials(authHeader);
            String email = credentials[0];
            String password = credentials[1];

            String passwordActual = passwordData.get("passwordActual");
            String passwordNuevo = passwordData.get("passwordNuevo");

            perfilService.cambiarPassword(email, password, passwordActual, passwordNuevo);
            return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/convertir-artesano")
    public ResponseEntity<?> convertirArtesano(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> artesanoData) {
        try {
            String[] credentials = extractCredentials(authHeader);
            String email = credentials[0];
            String password = credentials[1];

            ArtesanoResponse artesano = perfilService.convertirArtesano(email, password, artesanoData);
            return ResponseEntity.ok(artesano);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String[] extractCredentials(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
            return credentials.split(":", 2);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}
