package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.PaymentRequest;
import com.carolinacomba.marketplace.dto.PaymentResponse;
import com.carolinacomba.marketplace.dto.CreatePreferenceRequest;
import com.carolinacomba.marketplace.dto.PreferenceResponse;
import com.carolinacomba.marketplace.model.Usuario;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MercadoPagoService {
    
    /**
     * Crea una preferencia de pago para Checkout Pro
     * @param preferenceRequest Datos de la preferencia
     * @param usuario Usuario que realiza la compra
     * @return Respuesta con la preferencia creada
     */
    PreferenceResponse createPreference(CreatePreferenceRequest preferenceRequest, Usuario usuario) throws MPException, MPApiException;
    
    /**
     * Crea un pago directo (para Checkout API)
     * @param paymentRequest Datos del pago
     * @return Respuesta del pago
     */
    PaymentResponse createPayment(PaymentRequest paymentRequest);
    
    /**
     * Obtiene el estado de un pago
     * @param paymentId ID del pago
     * @return Estado del pago
     */
    String getPaymentStatus(String paymentId);
    
    /**
     * Consulta el estado de un pago por external_reference
     * @param externalReference Referencia externa
     * @return Estado del pago
     */
    String getPaymentStatusByReference(String externalReference);
    
    /**
     * Obtiene la clave pública de Mercado Pago
     * @return Clave pública
     */
    String getPublicKey();
    
    /**
     * Procesa una notificación de webhook de Mercado Pago
     * @param notification Datos de la notificación
     */
    void procesarNotificacion(String notification);
    
    
    /**
     * Lista todas las órdenes para debugging
     * @return Lista de órdenes
     */
    Object listarOrdenesParaDebug();
}
