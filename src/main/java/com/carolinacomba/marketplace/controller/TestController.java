package com.carolinacomba.marketplace.controller;

import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        boolean matches = passwordEncoder.matches("password", usuario.getContraseña());
        
        return String.format(
            "Email: %s, Contraseña hash: %s, Matches 'password': %s", 
            usuario.getEmail(), 
            usuario.getContraseña(), 
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
            boolean matches = passwordEncoder.matches(password, usuario.getContraseña());
            
            if (matches) {
                return String.format("Login exitoso para %s (rol: %s)", email, usuario.getRol());
            } else {
                return String.format("Contraseña incorrecta para %s", email);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
