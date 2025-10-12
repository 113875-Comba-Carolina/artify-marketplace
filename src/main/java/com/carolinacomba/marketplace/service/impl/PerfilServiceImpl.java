package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.ArtesanoResponse;
import com.carolinacomba.marketplace.dto.UsuarioResponse;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.service.IUsuarioService;
import com.carolinacomba.marketplace.service.PerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final ArtesanoRepository artesanoRepository;

    @Override
    public Object obtenerPerfil(String email, String password) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            List<Object[]> emprendimientoFieldsList = usuarioService.findEmprendimientoFieldsByEmail(email);
            if (emprendimientoFieldsList != null && !emprendimientoFieldsList.isEmpty()) {
                Object[] emprendimientoFields = emprendimientoFieldsList.get(0);
                for (int i = 0; i < emprendimientoFields.length; i++) {
                }
            }
        }

        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            
            List<Object[]> emprendimientoFieldsList = usuarioService.findEmprendimientoFieldsByEmail(email);
            String nombreEmprendimiento = null;
            String descripcion = null;
            String ubicacion = null;
            
            if (emprendimientoFieldsList != null && !emprendimientoFieldsList.isEmpty()) {
                Object[] emprendimientoFields = emprendimientoFieldsList.get(0);
                if (emprendimientoFields.length >= 3) {
                    nombreEmprendimiento = (String) emprendimientoFields[0];
                    descripcion = (String) emprendimientoFields[1];
                    ubicacion = (String) emprendimientoFields[2];
                }
            }
            
            ArtesanoResponse response = new ArtesanoResponse(usuario, nombreEmprendimiento, descripcion, ubicacion);
            return response;
        }

        return new UsuarioResponse(usuario);
    }

    @Override
    public Object actualizarPerfil(String email, String password, Map<String, Object> perfilData) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (perfilData.containsKey("nombre")) {
            usuario.setNombre((String) perfilData.get("nombre"));
        }

        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            String nombreEmprendimiento = (String) perfilData.get("nombreEmprendimiento");
            String descripcion = (String) perfilData.get("descripcion");
            String ubicacion = (String) perfilData.get("ubicacion");
            
            if (nombreEmprendimiento != null || descripcion != null || ubicacion != null) {
                List<Object[]> emprendimientoFields = usuarioService.findEmprendimientoFieldsByEmail(email);
                String currentNombreEmprendimiento = nombreEmprendimiento;
                String currentDescripcion = descripcion;
                String currentUbicacion = ubicacion;
                
                if (emprendimientoFields != null && !emprendimientoFields.isEmpty()) {
                    Object[] fields = emprendimientoFields.get(0);
                    if (fields.length >= 3) {
                        if (currentNombreEmprendimiento == null) currentNombreEmprendimiento = (String) fields[0];
                        if (currentDescripcion == null) currentDescripcion = (String) fields[1];
                        if (currentUbicacion == null) currentUbicacion = (String) fields[2];
                    }
                }
                
                artesanoRepository.updateEmprendimientoFields(
                    usuario.getId(),
                    currentNombreEmprendimiento,
                    currentDescripcion,
                    currentUbicacion
                );
            }
        }

        Usuario usuarioActualizado = usuarioService.save(usuario);

        if (usuarioActualizado.getRol() == Usuario.Rol.ARTESANO) {
            List<Object[]> emprendimientoFields = usuarioService.findEmprendimientoFieldsByEmail(email);
            String nombreEmprendimiento = null;
            String descripcion = null;
            String ubicacion = null;
            
            if (emprendimientoFields != null && !emprendimientoFields.isEmpty()) {
                Object[] fields = emprendimientoFields.get(0);
                if (fields.length >= 3) {
                    nombreEmprendimiento = (String) fields[0];
                    descripcion = (String) fields[1];
                    ubicacion = (String) fields[2];
                }
            }
            
            return new ArtesanoResponse(usuarioActualizado, nombreEmprendimiento, descripcion, ubicacion);
        }

        return new UsuarioResponse(usuarioActualizado);
    }

    @Override
    public void cambiarPassword(String email, String password, String passwordActual, String passwordNuevo) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!usuarioService.verificarPassword(usuario, passwordActual)) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        usuario.setContrasena(passwordEncoder.encode(passwordNuevo));
        usuarioService.save(usuario);
    }

    @Override
    @Transactional
    public ArtesanoResponse convertirArtesano(String email, String password, Map<String, String> artesanoData) {
        
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            throw new RuntimeException("El usuario ya es artesano");
        }

        try {
            usuario.setRol(Usuario.Rol.ARTESANO);

            Usuario usuarioActualizado = usuarioService.save(usuario);

            artesanoRepository.updateEmprendimientoFields(
                usuarioActualizado.getId(),
                artesanoData.get("nombreEmprendimiento"),
                artesanoData.get("descripcion"),
                artesanoData.get("ubicacion")
            );

            return new ArtesanoResponse(usuarioActualizado);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al convertir a artesano: " + e.getMessage());
        }
    }
}
