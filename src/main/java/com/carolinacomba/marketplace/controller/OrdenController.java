package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.OrdenResponse;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.OrdenService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;
    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> obtenerMisOrdenes() {
        try {
            Usuario usuario = obtenerUsuarioActual();
            List<OrdenResponse> ordenes = ordenService.obtenerOrdenesPorUsuario(usuario);
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{ordenId}")
    public ResponseEntity<OrdenResponse> obtenerOrden(@PathVariable Long ordenId) {
        try {
            Usuario usuario = obtenerUsuarioActual();
            OrdenResponse orden = ordenService.obtenerOrdenPorId(ordenId, usuario);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mercado-pago/{mercadoPagoId}")
    public ResponseEntity<OrdenResponse> obtenerOrdenPorMercadoPagoId(@PathVariable String mercadoPagoId) {
        try {
            OrdenResponse orden = ordenService.obtenerOrdenPorMercadoPagoId(mercadoPagoId);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioService.buscarPorEmail(email);
    }
}
