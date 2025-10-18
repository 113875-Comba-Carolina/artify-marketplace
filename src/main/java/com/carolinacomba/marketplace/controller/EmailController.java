package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.dto.EmailRequest;
import com.carolinacomba.marketplace.dto.OrderConfirmationEmailData;
import com.carolinacomba.marketplace.dto.WelcomeEmailData;
import com.carolinacomba.marketplace.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * Endpoint para enviar email de bienvenida de prueba
     */
    @PostMapping("/test/welcome")
    public ResponseEntity<?> testWelcomeEmail(@RequestBody Map<String, String> request) {
        try {
            WelcomeEmailData welcomeData = new WelcomeEmailData(
                request.get("nombre"),
                request.get("email"),
                request.get("tipoUsuario")
            );
            
            emailService.sendWelcomeEmail(welcomeData);
            return ResponseEntity.ok(Map.of("message", "Email de bienvenida enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para enviar email de confirmación de orden de prueba
     */
    @PostMapping("/test/order-confirmation")
    public ResponseEntity<?> testOrderConfirmationEmail(@RequestBody Map<String, String> request) {
        try {
            // Crear datos de prueba para la orden
            OrderConfirmationEmailData.OrderItemData item1 = new OrderConfirmationEmailData.OrderItemData(
                "Cerámica Artesanal Única", 2, new BigDecimal("150.00"), "Carolina Comba"
            );
            OrderConfirmationEmailData.OrderItemData item2 = new OrderConfirmationEmailData.OrderItemData(
                "Textil Tejido a Mano", 1, new BigDecimal("200.00"), "Elena Morales"
            );

            OrderConfirmationEmailData orderData = new OrderConfirmationEmailData(
                request.get("nombreCliente"),
                request.get("email"),
                Long.parseLong(request.getOrDefault("numeroOrden", "12345")),
                LocalDateTime.now(),
                new BigDecimal("500.00"),
                Arrays.asList(item1, item2),
                request.getOrDefault("direccionEnvio", "Calle Falsa 123, Ciudad"),
                "CONFIRMADA"
            );

            emailService.sendOrderConfirmationEmail(orderData);
            return ResponseEntity.ok(Map.of("message", "Email de confirmación de orden enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para enviar email de actualización de estado de prueba
     */
    @PostMapping("/test/order-status-update")
    public ResponseEntity<?> testOrderStatusUpdateEmail(@RequestBody Map<String, String> request) {
        try {
            emailService.sendOrderStatusUpdateEmail(
                request.get("email"),
                request.get("nombre"),
                Long.parseLong(request.getOrDefault("numeroOrden", "12345")),
                request.getOrDefault("nuevoEstado", "EN_PROCESO")
            );
            return ResponseEntity.ok(Map.of("message", "Email de actualización de estado enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para enviar email de notificación de nueva venta de prueba
     */
    @PostMapping("/test/new-sale-notification")
    public ResponseEntity<?> testNewSaleNotificationEmail(@RequestBody Map<String, String> request) {
        try {
            emailService.sendNewSaleNotificationEmail(
                request.get("artesanoEmail"),
                request.get("artesanoNombre"),
                request.get("productoNombre"),
                Integer.parseInt(request.getOrDefault("cantidad", "1")),
                request.get("clienteNombre")
            );
            return ResponseEntity.ok(Map.of("message", "Email de notificación de nueva venta enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para enviar email genérico de prueba
     */
    @PostMapping("/test/generic")
    public ResponseEntity<?> testGenericEmail(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> variables = new HashMap<>();
            if (request.containsKey("variables") && request.get("variables") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tempVariables = (Map<String, Object>) request.get("variables");
                variables = tempVariables;
            }

            EmailRequest emailRequest = new EmailRequest(
                (String) request.get("to"),
                (String) request.get("subject"),
                (String) request.get("templateName"),
                variables
            );

            emailService.sendEmail(emailRequest);
            return ResponseEntity.ok(Map.of("message", "Email genérico enviado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener información sobre los templates disponibles
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getAvailableTemplates() {
        Map<String, Object> templates = new HashMap<>();
        templates.put("welcome", "Email de bienvenida para nuevos usuarios");
        templates.put("order-confirmation", "Email de confirmación de orden");
        templates.put("order-status-update", "Email de actualización de estado de orden");
        templates.put("new-sale-notification", "Email de notificación de nueva venta para artesanos");
        
        return ResponseEntity.ok(templates);
    }

    /**
     * Endpoint para verificar configuración de email (solo para desarrollo)
     */
    @GetMapping("/config")
    public ResponseEntity<?> getEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("mailUsername", System.getenv("MAIL_USERNAME"));
        config.put("mailPassword", System.getenv("MAIL_PASSWORD") != null ? "***DEFINIDA***" : "NO DEFINIDA");
        config.put("frontendUrl", System.getenv("FRONTEND_URL"));
        
        return ResponseEntity.ok(config);
    }
}
