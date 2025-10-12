package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.MercadoPagoService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private MercadoPagoService mercadoPagoService;
    
    @Autowired
    private IUsuarioService usuarioService;

    /**
     * Crea una preferencia de pago para Checkout Pro
     * @param preferenceRequest Datos de la preferencia
     * @return Respuesta con la preferencia creada
     */
    @PostMapping("/preference")
    public ResponseEntity<?> createPreference(@Valid @RequestBody CreatePreferenceRequest preferenceRequest, HttpServletRequest request) {
        try {
            // Obtener usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email);
            
            PreferenceResponse response = mercadoPagoService.createPreference(preferenceRequest, usuario);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (MPApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getApiResponse().getContent());
        } catch (MPException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Crea un pago directo (para Checkout API)
     * @param paymentRequest Datos del pago
     * @return Respuesta del pago
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = mercadoPagoService.createPayment(paymentRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtiene el estado de un pago
     * @param paymentId ID del pago
     * @return Estado del pago
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String paymentId) {
        String status = mercadoPagoService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(status);
    }

    /**
     * Consulta el estado de un pago por external_reference
     * @param externalReference Referencia externa
     * @return Estado del pago
     */
    @GetMapping("/status-by-reference/{externalReference}")
    public ResponseEntity<?> getPaymentStatusByReference(@PathVariable String externalReference) {
        try {
            String status = mercadoPagoService.getPaymentStatusByReference(externalReference);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error consultando pago: " + e.getMessage());
        }
    }

    /**
     * Lista todas las órdenes para debugging
     * @return Lista de órdenes
     */
    @GetMapping("/debug/ordenes")
    public ResponseEntity<?> listarOrdenes() {
        try {
            Object ordenes = mercadoPagoService.listarOrdenesParaDebug();
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error listando órdenes: " + e.getMessage());
        }
    }

    /**
     * Obtiene la clave pública de Mercado Pago
     * @return Clave pública
     */
    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        String publicKey = mercadoPagoService.getPublicKey();
        return ResponseEntity.ok(publicKey);
    }

    /**
     * Configura el webhook de Mercado Pago
     * @return Respuesta de configuración
     */
    @PostMapping("/configure-webhook")
    public ResponseEntity<String> configureWebhook() {
        try {
            String webhookUrl = "http://localhost:8080/api/payments/webhook";
            mercadoPagoService.configureWebhook(webhookUrl);
            return ResponseEntity.ok("Webhook configurado exitosamente: " + webhookUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error configurando webhook: " + e.getMessage());
        }
    }

    /**
     * Webhook para recibir notificaciones de Mercado Pago
     * @param notification Datos de la notificación
     * @return Respuesta de confirmación
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String notification) {
        try {
            System.out.println("=== WEBHOOK RECIBIDO ===");
            System.out.println("Notificación: " + notification);
            
            // Procesar la notificación de Mercado Pago
            mercadoPagoService.procesarNotificacion(notification);
            
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            System.out.println("Error procesando webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
