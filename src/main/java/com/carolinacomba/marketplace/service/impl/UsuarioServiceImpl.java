package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.dto.PasswordResetEmailData;
import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroUsuarioRequest;
import com.carolinacomba.marketplace.dto.WelcomeEmailData;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import com.carolinacomba.marketplace.service.EmailService;
import com.carolinacomba.marketplace.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ArtesanoRepository artesanoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

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
        usuario.setTelefono(request.getTelefono());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Enviar email de bienvenida
        try {
            WelcomeEmailData welcomeData = new WelcomeEmailData(
                usuario.getNombre(),
                usuario.getEmail(),
                "USUARIO"
            );
            emailService.sendWelcomeEmail(welcomeData);
        } catch (Exception e) {
            // Log el error pero no fallar el registro
            System.err.println("Error enviando email de bienvenida: " + e.getMessage());
        }

        return usuarioGuardado;
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
        artesano.setTelefono(request.getTelefono());

        Artesano artesanoGuardado = artesanoRepository.save(artesano);

        // Enviar email de bienvenida para artesanos
        try {
            WelcomeEmailData welcomeData = new WelcomeEmailData(
                artesano.getNombre(),
                artesano.getEmail(),
                "ARTESANO"
            );
            emailService.sendWelcomeEmail(welcomeData);
        } catch (Exception e) {
            // Log el error pero no fallar el registro
            System.err.println("Error enviando email de bienvenida: " + e.getMessage());
        }

        return artesanoGuardado;
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

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public List<Object[]> findEmprendimientoFieldsByEmail(String email) {
        return usuarioRepository.findEmprendimientoFieldsByEmail(email);
    }

    @Override
    public boolean verificarPassword(Usuario usuario, String password) {
        return passwordEncoder.matches(password, usuario.getContrasena());
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public void createPasswordResetToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No existe un usuario con ese correo electrónico"));
        
        // Generar token único
        String token = UUID.randomUUID().toString();
        
        // Guardar token y fecha de expiración (1 hora)
        usuario.setResetPasswordToken(token);
        usuario.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);
        
        // Construir link de reseteo
        String resetLink = frontendUrl + "/auth/reset-password?token=" + token;
        
        // Enviar email
        try {
            PasswordResetEmailData resetData = new PasswordResetEmailData(
                usuario.getEmail(),
                usuario.getNombre(),
                resetLink
            );
            emailService.sendPasswordResetEmail(resetData);
        } catch (Exception e) {
            System.err.println("Error enviando email de recuperación: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        Usuario usuario = usuarioRepository.findByResetPasswordToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido"));
        
        // Verificar que el token no haya expirado
        if (usuario.getResetPasswordTokenExpiry() == null || 
            usuario.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }
        
        // Actualizar contraseña
        usuario.setContrasena(passwordEncoder.encode(newPassword));
        
        // Limpiar token
        usuario.setResetPasswordToken(null);
        usuario.setResetPasswordTokenExpiry(null);
        
        usuarioRepository.save(usuario);
    }
}
