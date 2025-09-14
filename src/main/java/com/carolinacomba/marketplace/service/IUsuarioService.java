package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroUsuarioRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;

public interface IUsuarioService {
    
    Usuario buscarPorEmail(String email);
    
    Usuario autenticar(String email, String password);
    
    Usuario registrarUsuario(RegistroUsuarioRequest request);
    
    Artesano registrarArtesano(RegistroArtesanoRequest request);
    
    Usuario buscarUsuarioPorId(Long id);
    
    Artesano buscarArtesanoPorId(Long id);
    
    Artesano buscarArtesanoPorEmail(String email);
    
    Usuario cambiarRol(String email, CambioRolRequest request);
}
