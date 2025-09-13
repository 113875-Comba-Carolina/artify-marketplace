package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroClienteRequest;
import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Cliente;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.repository.ClienteRepository;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ArtesanoRepository artesanoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    public Usuario autenticar(String email, String password) {
        Usuario usuario = buscarPorEmail(email);
        
        if (!passwordEncoder.matches(password, usuario.getContraseña())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        return usuario;
    }

    public Cliente registrarCliente(RegistroClienteRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email");
        }

        // Crear nuevo cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setContraseña(passwordEncoder.encode(request.getPassword()));
        cliente.setRol(Usuario.Rol.CLIENTE);

        return clienteRepository.save(cliente);
    }

    public Artesano registrarArtesano(RegistroArtesanoRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email");
        }

        // Crear nuevo artesano
        Artesano artesano = new Artesano();
        artesano.setNombre(request.getNombre());
        artesano.setEmail(request.getEmail());
        artesano.setContraseña(passwordEncoder.encode(request.getPassword()));
        artesano.setRol(Usuario.Rol.ARTESANO);
        artesano.setNombreEmprendimiento(request.getNombreEmprendimiento());
        artesano.setDescripcion(request.getDescripcion());
        artesano.setUbicacion(request.getUbicacion());

        return artesanoRepository.save(artesano);
    }

    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    public Artesano buscarArtesanoPorId(Long id) {
        return artesanoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artesano no encontrado con ID: " + id));
    }

    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));
    }

    public Artesano buscarArtesanoPorEmail(String email) {
        return artesanoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Artesano no encontrado con email: " + email));
    }

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
        
        // Si está cambiando a ARTESANO, crear entidad Artesano
        if (nuevoRol == Usuario.Rol.ARTESANO && usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            Artesano artesano = new Artesano();
            artesano.setId(cliente.getId());
            artesano.setNombre(cliente.getNombre());
            artesano.setEmail(cliente.getEmail());
            artesano.setContraseña(cliente.getContraseña());
            artesano.setRol(Usuario.Rol.ARTESANO);
            artesano.setNombreEmprendimiento("Mi Emprendimiento");
            artesano.setDescripcion("Descripción de mi emprendimiento");
            
            // Eliminar cliente y crear artesano
            clienteRepository.delete(cliente);
            return artesanoRepository.save(artesano);
        }
        
        // Si está cambiando a CLIENTE, crear entidad Cliente
        if (nuevoRol == Usuario.Rol.CLIENTE && usuario instanceof Artesano) {
            Artesano artesano = (Artesano) usuario;
            Cliente cliente = new Cliente();
            cliente.setId(artesano.getId());
            cliente.setNombre(artesano.getNombre());
            cliente.setEmail(artesano.getEmail());
            cliente.setContraseña(artesano.getContraseña());
            cliente.setRol(Usuario.Rol.CLIENTE);
            
            // Eliminar artesano y crear cliente
            artesanoRepository.delete(artesano);
            return clienteRepository.save(cliente);
        }
        
        return usuarioRepository.save(usuario);
    }
} 