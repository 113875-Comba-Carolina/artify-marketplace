package com.carolinacomba.marketplace.controller.test;

import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios")
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/usuario/{email}")
    public Usuario getUsuarioByEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.orElse(null);
    }
    
    @GetMapping("/test-password/{email}")
    public String testPassword(@PathVariable String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return "Usuario no encontrado";
        }
        
        Usuario usuario = usuarioOpt.get();
        boolean matches = passwordEncoder.matches("password", usuario.getContrasena());
        
        return String.format(
            "Email: %s, Contrasena hash: %s, Matches 'password': %s", 
            usuario.getEmail(), 
            usuario.getContrasena(), 
            matches
        );
    }
    
    @GetMapping("/test-login/{email}/{password}")
    public String testLogin(@PathVariable String email, @PathVariable String password) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                return "Usuario no encontrado: " + email;
            }
            
            Usuario usuario = usuarioOpt.get();
            boolean matches = passwordEncoder.matches(password, usuario.getContrasena());
            
            if (matches) {
                return String.format("Login exitoso para %s (rol: %s)", email, usuario.getRol());
            } else {
                return String.format("Contrasena incorrecta para %s", email);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
