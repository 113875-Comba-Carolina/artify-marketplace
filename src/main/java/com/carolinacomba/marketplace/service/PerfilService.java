package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.ArtesanoResponse;
import com.carolinacomba.marketplace.dto.UsuarioResponse;

import java.util.Map;

public interface PerfilService {
    
    Object obtenerPerfil(String email, String password);
    
    Object actualizarPerfil(String email, String password, Map<String, Object> perfilData);
    
    void cambiarPassword(String email, String password, String passwordActual, String passwordNuevo);
    
    ArtesanoResponse convertirArtesano(String email, String password, Map<String, String> artesanoData);
}
