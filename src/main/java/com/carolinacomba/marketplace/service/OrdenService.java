package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.OrdenResponse;
import com.carolinacomba.marketplace.dto.CarritoItem;
import com.carolinacomba.marketplace.model.Orden;
import com.carolinacomba.marketplace.model.Usuario;

import java.util.List;

public interface OrdenService {
    
    Orden crearOrden(Usuario usuario, String externalReference, List<CarritoItem> items);
    
    Orden actualizarEstadoOrden(String mercadoPagoId, String estado);
    
    List<OrdenResponse> obtenerOrdenesPorUsuario(Usuario usuario);
    
    OrdenResponse obtenerOrdenPorId(Long ordenId, Usuario usuario);
    
    OrdenResponse obtenerOrdenPorMercadoPagoId(String mercadoPagoId);
}
