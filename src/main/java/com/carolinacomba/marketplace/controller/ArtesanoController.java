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

    /**
     * Endpoint de prueba para verificar la consulta de estadísticas
     */
    @GetMapping("/test-stats")
    public ResponseEntity<?> testStats() {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            // Ejecutar consulta directamente
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


    /**
     * Obtiene todas las ventas del artesano autenticado
     */
    @GetMapping("/ventas")
    public ResponseEntity<?> obtenerMisVentas() {
        try {
            System.out.println("=== OBTENER VENTAS ===");
            
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            System.out.println("Email autenticado: " + email);
            
            Usuario artesano = usuarioService.buscarPorEmail(email);
            System.out.println("Artesano encontrado: " + (artesano != null ? artesano.getNombre() : "null"));
            
            if (artesano == null) {
                System.out.println("ERROR: Usuario no encontrado");
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            // Verificar que es artesano
            System.out.println("Rol del usuario: " + artesano.getRol());
            if (!"ARTESANO".equals(artesano.getRol().toString())) {
                System.out.println("ERROR: No es artesano");
                return ResponseEntity.badRequest().body("Acceso denegado: Solo artesanos pueden acceder a esta información");
            }
            
            System.out.println("Obteniendo ventas para artesano ID: " + artesano.getId());
            List<Map<String, Object>> ventas = artesanoService.obtenerVentasPorArtesano(artesano);
            System.out.println("Ventas encontradas: " + ventas.size());
            
            return ResponseEntity.ok(ventas);
            
        } catch (Exception e) {
            System.out.println("ERROR en obtenerMisVentas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error obteniendo ventas: " + e.getMessage());
        }
    }

    /**
     * Obtiene las ventas del artesano filtradas por estado
     */
    @GetMapping("/ventas/estado/{estado}")
    public ResponseEntity<?> obtenerVentasPorEstado(@PathVariable String estado) {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            // Verificar que es artesano
            if (!"ARTESANO".equals(artesano.getRol().toString())) {
                return ResponseEntity.badRequest().body("Acceso denegado: Solo artesanos pueden acceder a esta información");
            }
            
            List<Map<String, Object>> ventas = artesanoService.obtenerVentasPorArtesanoYEstado(artesano, estado);
            return ResponseEntity.ok(ventas);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo ventas: " + e.getMessage());
        }
    }

    /**
     * Obtiene estadísticas de ventas del artesano
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario artesano = usuarioService.buscarPorEmail(email);
            
            if (artesano == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            
            // Verificar que es artesano
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
