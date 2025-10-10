package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroUsuarioRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import com.carolinacomba.marketplace.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ArtesanoRepository artesanoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    @Override
    public Usuario autenticar(String email, String password) {
        Usuario usuario = buscarPorEmail(email);
        
        if (!passwordEncoder.matches(password, usuario.getContrasena())) {
            throw new RuntimeException("Contrasena incorrecta");
        }
        
        return usuario;
    }

    @Override
    public Usuario registrarUsuario(RegistroUsuarioRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContrasena(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Usuario.Rol.USUARIO);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Artesano registrarArtesano(RegistroArtesanoRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email");
        }

        // Crear nuevo artesano
        Artesano artesano = new Artesano();
        artesano.setNombre(request.getNombre());
        artesano.setEmail(request.getEmail());
        artesano.setContrasena(passwordEncoder.encode(request.getPassword()));
        artesano.setRol(Usuario.Rol.ARTESANO);
        artesano.setNombreEmprendimiento(request.getNombreEmprendimiento());
        artesano.setDescripcion(request.getDescripcion());
        artesano.setUbicacion(request.getUbicacion());

        return artesanoRepository.save(artesano);
    }

    @Override
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public Artesano buscarArtesanoPorId(Long id) {
        return artesanoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artesano no encontrado con ID: " + id));
    }

    @Override
    public Artesano buscarArtesanoPorEmail(String email) {
        return artesanoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Artesano no encontrado con email: " + email));
    }

    @Override
    public Usuario cambiarRol(String email, CambioRolRequest request) {
        Usuario usuario = buscarPorEmail(email);
        
        // Validar que el usuario no sea ADMIN
        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            throw new RuntimeException("No se puede cambiar el rol de un administrador");
        }
        
        // Validar que el nuevo rol sea válido
        Usuario.Rol nuevoRol;
        try {
            nuevoRol = Usuario.Rol.valueOf(request.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + request.getRol());
        }
        
        // Si ya tiene ese rol, no hacer nada
        if (usuario.getRol() == nuevoRol) {
            return usuario;
        }
        
        // Cambiar el rol
        usuario.setRol(nuevoRol);
        
        return usuarioRepository.save(usuario);
    }
}
