package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.service.MercadoPagoService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
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
    public ResponseEntity<?> createPreference(@Valid @RequestBody CreatePreferenceRequest preferenceRequest) {
        try {
            System.out.println("=== DEBUG: Datos recibidos ===");
            System.out.println("Items: " + preferenceRequest.getItems());
            System.out.println("External Reference: " + preferenceRequest.getExternalReference());
            System.out.println("Success URL: " + preferenceRequest.getSuccessUrl());
            System.out.println("Failure URL: " + preferenceRequest.getFailureUrl());
            System.out.println("Pending URL: " + preferenceRequest.getPendingUrl());
            System.out.println("Auto Return: " + preferenceRequest.getAutoReturn());
            
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
     * Obtiene la clave pública de Mercado Pago
     * @return Clave pública
     */
    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        String publicKey = mercadoPagoService.getPublicKey();
        return ResponseEntity.ok(publicKey);
    }

    /**
     * Webhook para recibir notificaciones de Mercado Pago
     * @param notification Datos de la notificación
     * @return Respuesta de confirmación
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String notification) {
        // Aquí procesarías la notificación de Mercado Pago
        // Por ejemplo, actualizar el estado de una orden en tu base de datos
        System.out.println("Notificación recibida: " + notification);
        return ResponseEntity.ok("OK");
    }
}
