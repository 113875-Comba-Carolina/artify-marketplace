package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.EmailRequest;
import com.carolinacomba.marketplace.dto.OrderConfirmationEmailData;
import com.carolinacomba.marketplace.dto.PasswordResetEmailData;
import com.carolinacomba.marketplace.dto.WelcomeEmailData;
import com.carolinacomba.marketplace.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.frontend.url:https://treasurable-almeda-unsimply.ngrok-free.dev}")
    private String frontendUrl;

    @Override
    public void sendEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());

            // Procesar template de Thymeleaf
            Context context = new Context();
            if (emailRequest.getVariables() != null) {
                context.setVariables(emailRequest.getVariables());
            }

            String htmlContent = templateEngine.process(emailRequest.getTemplateName(), context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", emailRequest.getTo());

        } catch (MessagingException e) {
            log.error("Error enviando email a {}: {}", emailRequest.getTo(), e.getMessage());
            throw new RuntimeException("Error enviando email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(WelcomeEmailData welcomeData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", welcomeData.getNombre());
        variables.put("email", welcomeData.getEmail());
        variables.put("tipoUsuario", welcomeData.getTipoUsuario());
        variables.put("frontendUrl", frontendUrl);

        EmailRequest emailRequest = new EmailRequest(
            welcomeData.getEmail(),
            "¡Bienvenido a Artify Marketplace!",
            "welcome",
            variables
        );

        sendEmail(emailRequest);
    }

    @Override
    public void sendOrderConfirmationEmail(OrderConfirmationEmailData orderData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreCliente", orderData.getNombreCliente());
        variables.put("numeroOrden", orderData.getNumeroOrden());
        variables.put("fechaOrden", orderData.getFechaOrden());
        variables.put("total", orderData.getTotal());
        variables.put("items", orderData.getItems());
        variables.put("direccionEnvio", orderData.getDireccionEnvio());
        variables.put("estadoOrden", orderData.getEstadoOrden());
        variables.put("frontendUrl", frontendUrl);

        EmailRequest emailRequest = new EmailRequest(
            orderData.getEmail(),
            "Confirmación de Orden #" + orderData.getNumeroOrden(),
            "order-confirmation",
            variables
        );

        sendEmail(emailRequest);
    }

    @Override
    public void sendNewSaleNotificationEmail(String artesanoEmail, String artesanoNombre, String productoNombre, Integer cantidad, String clienteNombre) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("artesanoNombre", artesanoNombre);
        variables.put("productoNombre", productoNombre);
        variables.put("cantidad", cantidad);
        variables.put("clienteNombre", clienteNombre);
        variables.put("frontendUrl", frontendUrl);

        EmailRequest emailRequest = new EmailRequest(
            artesanoEmail,
            "¡Nueva venta en tu emprendimiento!",
            "new-sale-notification",
            variables
        );

        sendEmail(emailRequest);
    }

    @Override
    public void sendPasswordResetEmail(PasswordResetEmailData resetData) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", resetData.getUserName());
        variables.put("resetLink", resetData.getResetLink());
        variables.put("frontendUrl", frontendUrl);

        EmailRequest emailRequest = new EmailRequest(
            resetData.getEmail(),
            "Recuperación de contraseña - Artify",
            "password-reset",
            variables
        );

        sendEmail(emailRequest);
    }
}
