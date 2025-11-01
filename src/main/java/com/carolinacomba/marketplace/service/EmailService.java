package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.EmailRequest;
import com.carolinacomba.marketplace.dto.OrderConfirmationEmailData;
import com.carolinacomba.marketplace.dto.PasswordResetEmailData;
import com.carolinacomba.marketplace.dto.WelcomeEmailData;

public interface EmailService {
    
    /**
     * Envía un email genérico usando un template de Thymeleaf
     */
    void sendEmail(EmailRequest emailRequest);
    
    /**
     * Envía email de bienvenida a un nuevo usuario
     */
    void sendWelcomeEmail(WelcomeEmailData welcomeData);
    
    /**
     * Envía email de confirmación de orden
     */
    void sendOrderConfirmationEmail(OrderConfirmationEmailData orderData);
    
    /**
     * Envía email de notificación a artesano sobre nueva venta
     */
    void sendNewSaleNotificationEmail(String artesanoEmail, String artesanoNombre, String productoNombre, Integer cantidad, String clienteNombre);
    
    /**
     * Envía email de recuperación de contraseña
     */
    void sendPasswordResetEmail(PasswordResetEmailData resetData);
}
