package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import java.util.List;
import java.util.Map;

public interface ArtesanoService {
    
    Artesano findById(Long id);
    
    Artesano save(Artesano artesano);
    
    // Métodos para ventas del artesano
    List<Map<String, Object>> obtenerVentasPorArtesano(Usuario artesano);
    
    List<Map<String, Object>> obtenerVentasPorArtesanoYEstado(Usuario artesano, String estado);
    
    Map<String, Object> obtenerEstadisticasVentas(Usuario artesano);
}
