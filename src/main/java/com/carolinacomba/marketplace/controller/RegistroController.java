package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registro")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RegistroController {

    private final IUsuarioService usuarioService;

    @PostMapping("/usuario")
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

    @PostMapping("/artesano")
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
}
