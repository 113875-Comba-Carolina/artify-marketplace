package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.ArtesanoService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import com.carolinacomba.marketplace.repository.ItemOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artesano")
public class ArtesanoController {

    @Autowired
    private ArtesanoService artesanoService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private ItemOrdenRepository itemOrdenRepository;

    @GetMapping("/test-stats")
    public ResponseEntity<?> testStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            Object[] stats = itemOrdenRepository.findEstadisticasVentasPorArtesano(artesano.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("rawStats", stats);
            result.put("statsLength", stats != null ? stats.length : "null");
            result.put("statsArray", stats != null ? java.util.Arrays.toString(stats) : "null");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/ventas")
    public ResponseEntity<?> obtenerMisVentas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Usuario artesano = usuarioService.buscarPorEmail(email);

            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            if (!"ARTESANO".equals(artesano.getRol().toString())) {
                return ResponseEntity.badRequest().body("Acceso denegado: Solo artesanos pueden acceder a esta información");
            }
            
            List<Map<String, Object>> ventas = artesanoService.obtenerVentasPorArtesano(artesano);

            return ResponseEntity.ok(ventas);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error obteniendo ventas: " + e.getMessage());
        }
    }

    @GetMapping("/ventas/estado/{estado}")
    public ResponseEntity<?> obtenerVentasPorEstado(@PathVariable String estado) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            if (!"ARTESANO".equals(artesano.getRol().toString())) {
                return ResponseEntity.badRequest().body("Acceso denegado: Solo artesanos pueden acceder a esta información");
            }
            
            List<Map<String, Object>> ventas = artesanoService.obtenerVentasPorArtesanoYEstado(artesano, estado);
            return ResponseEntity.ok(ventas);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo ventas: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            if (!"ARTESANO".equals(artesano.getRol().toString())) {
                return ResponseEntity.badRequest().body("Acceso denegado: Solo artesanos pueden acceder a esta información");
            }
            
            Map<String, Object> stats = artesanoService.obtenerEstadisticasVentas(artesano);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo estadísticas: " + e.getMessage());
        }
    }
}
