package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.ArtesanoResponse;
import com.carolinacomba.marketplace.dto.UsuarioResponse;
import com.carolinacomba.marketplace.model.Usuario;
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

    @Override
    public Object obtenerPerfil(String email, String password) {
        // Buscar usuario por email
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        System.out.println("=== DEBUG USUARIO ENCONTRADO ===");
        System.out.println("Usuario ID: " + usuario.getId());
        System.out.println("Usuario Email: " + usuario.getEmail());
        System.out.println("Usuario Rol: " + usuario.getRol());
        System.out.println("Nombre Emprendimiento desde BD: '" + usuario.getNombreEmprendimiento() + "'");
        System.out.println("Descripción desde BD: '" + usuario.getDescripcion() + "'");
        System.out.println("Ubicación desde BD: '" + usuario.getUbicacion() + "'");
        
        // Si es artesano y los campos están null, cargar desde consulta SQL nativa
        if (usuario.getRol() == Usuario.Rol.ARTESANO && usuario.getNombreEmprendimiento() == null) {
            System.out.println("=== CARGANDO CAMPOS DESDE CONSULTA SQL ===");
            List<Object[]> emprendimientoFieldsList = usuarioService.findEmprendimientoFieldsByEmail(email);
            System.out.println("Resultado de consulta SQL: " + (emprendimientoFieldsList != null ? "No es null" : "Es null"));
            if (emprendimientoFieldsList != null && !emprendimientoFieldsList.isEmpty()) {
                Object[] emprendimientoFields = emprendimientoFieldsList.get(0);
                System.out.println("Longitud del array: " + emprendimientoFields.length);
                for (int i = 0; i < emprendimientoFields.length; i++) {
                    System.out.println("Campo " + i + ": '" + emprendimientoFields[i] + "'");
                }
                if (emprendimientoFields.length >= 3) {
                    usuario.setNombreEmprendimiento((String) emprendimientoFields[0]);
                    usuario.setDescripcion((String) emprendimientoFields[1]);
                    usuario.setUbicacion((String) emprendimientoFields[2]);
                    System.out.println("Campos cargados desde SQL - Nombre Emprendimiento: '" + usuario.getNombreEmprendimiento() + "'");
                    System.out.println("Campos cargados desde SQL - Descripción: '" + usuario.getDescripcion() + "'");
                    System.out.println("Campos cargados desde SQL - Ubicación: '" + usuario.getUbicacion() + "'");
                }
            }
        }

        // Verificar contraseña
        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Si es artesano, devolver información completa
        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            System.out.println("=== DEBUG OBTENER PERFIL ===");
            System.out.println("Usuario ID: " + usuario.getId());
            System.out.println("Usuario Email: " + usuario.getEmail());
            System.out.println("Usuario Rol: " + usuario.getRol());
            System.out.println("Nombre Emprendimiento: '" + usuario.getNombreEmprendimiento() + "'");
            System.out.println("Descripción: '" + usuario.getDescripcion() + "'");
            System.out.println("Ubicación: '" + usuario.getUbicacion() + "'");
            System.out.println("¿Nombre Emprendimiento es null? " + (usuario.getNombreEmprendimiento() == null));
            System.out.println("¿Descripción es null? " + (usuario.getDescripcion() == null));
            System.out.println("¿Ubicación es null? " + (usuario.getUbicacion() == null));
            
            // Siempre devolver desde Usuario ya que no creamos registros separados en Artesano
            System.out.println("Devolviendo ArtesanoResponse desde Usuario");
            ArtesanoResponse response = new ArtesanoResponse(usuario);
            System.out.println("Response creado - Nombre Emprendimiento: '" + response.getNombreEmprendimiento() + "'");
            return response;
        }

        // Si es usuario normal, devolver información básica
        return new UsuarioResponse(usuario);
    }

    @Override
    public Object actualizarPerfil(String email, String password, Map<String, Object> perfilData) {
        // Buscar usuario por email
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificar contraseña
        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Actualizar datos básicos del usuario
        if (perfilData.containsKey("nombre")) {
            usuario.setNombre((String) perfilData.get("nombre"));
        }

        // Si es artesano, actualizar también los datos específicos directamente en el Usuario
        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            if (perfilData.containsKey("nombreEmprendimiento")) {
                usuario.setNombreEmprendimiento((String) perfilData.get("nombreEmprendimiento"));
            }
            if (perfilData.containsKey("descripcion")) {
                usuario.setDescripcion((String) perfilData.get("descripcion"));
            }
            if (perfilData.containsKey("ubicacion")) {
                usuario.setUbicacion((String) perfilData.get("ubicacion"));
            }
        }

        Usuario usuarioActualizado = usuarioService.save(usuario);

        // Si es artesano, devolver ArtesanoResponse
        if (usuarioActualizado.getRol() == Usuario.Rol.ARTESANO) {
            return new ArtesanoResponse(usuarioActualizado);
        }

        return new UsuarioResponse(usuarioActualizado);
    }

    @Override
    public void cambiarPassword(String email, String password, String passwordActual, String passwordNuevo) {
        // Buscar usuario por email
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificar contraseña actual
        if (!usuarioService.verificarPassword(usuario, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Verificar que la contraseña actual coincida con la enviada
        if (!usuarioService.verificarPassword(usuario, passwordActual)) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        // Cambiar contraseña (usando el nombre correcto de la propiedad)
        usuario.setContrasena(passwordEncoder.encode(passwordNuevo));
        usuarioService.save(usuario);
    }

    @Override
    @Transactional
    public ArtesanoResponse convertirArtesano(String email, String password, Map<String, String> artesanoData) {
        System.out.println("=== INICIO CONVERSIÓN A ARTESANO ===");
        System.out.println("Email: " + email);
        System.out.println("Datos artesano: " + artesanoData);
        
        // Buscar usuario por email
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            System.out.println("ERROR: Usuario no encontrado");
            throw new RuntimeException("Usuario no encontrado");
        }
        System.out.println("Usuario encontrado - ID: " + usuario.getId() + ", Rol: " + usuario.getRol());

        // Verificar contraseña
        if (!usuarioService.verificarPassword(usuario, password)) {
            System.out.println("ERROR: Credenciales inválidas");
            throw new RuntimeException("Credenciales inválidas");
        }

        // Verificar que no sea ya artesano
        if (usuario.getRol() == Usuario.Rol.ARTESANO) {
            System.out.println("ERROR: El usuario ya es artesano");
            throw new RuntimeException("El usuario ya es artesano");
        }

        try {
            // Actualizar el usuario existente con los datos de artesano
            System.out.println("Actualizando usuario a artesano...");
            usuario.setRol(Usuario.Rol.ARTESANO);
            usuario.setNombreEmprendimiento(artesanoData.get("nombreEmprendimiento"));
            usuario.setDescripcion(artesanoData.get("descripcion"));
            usuario.setUbicacion(artesanoData.get("ubicacion"));

            System.out.println("Guardando usuario actualizado...");
            Usuario usuarioActualizado = usuarioService.save(usuario);
            System.out.println("Usuario actualizado - ID: " + usuarioActualizado.getId() + ", Rol: " + usuarioActualizado.getRol());

            // No crear registro separado en tabla Artesano - usar solo la tabla Usuario
            System.out.println("Usuario convertido a artesano exitosamente");

            System.out.println("=== CONVERSIÓN EXITOSA ===");
            return new ArtesanoResponse(usuarioActualizado);
            
        } catch (Exception e) {
            System.out.println("ERROR durante la conversión: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al convertir a artesano: " + e.getMessage());
        }
    }
}
