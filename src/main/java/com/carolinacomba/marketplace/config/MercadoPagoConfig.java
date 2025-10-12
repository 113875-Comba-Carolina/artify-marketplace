package com.carolinacomba.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfig {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.public.key}")
    private String publicKey;

    @Value("${mercadopago.environment}")
    private String environment;

    @PostConstruct
    public void init() {
        // Configurar el access token de Mercado Pago
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Bean
    @Primary
    public String mercadoPagoPublicKey() {
        return publicKey;
    }

    @Bean
    public String mercadoPagoEnvironment() {
        return environment;
    }
}
