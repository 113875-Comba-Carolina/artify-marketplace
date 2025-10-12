package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.AdminService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/reportes")
    public ResponseEntity<?> obtenerReportes() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario admin = usuarioService.buscarPorEmail(email);
            
            if (admin == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            if (!"ADMIN".equals(admin.getRol().toString())) {
                return ResponseEntity.badRequest().body("Acceso denegado: Solo administradores pueden acceder a esta información");
            }
            
            Map<String, Object> reportes = adminService.obtenerReportesCompletos();
            return ResponseEntity.ok(reportes);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo reportes: " + e.getMessage());
        }
    }
}