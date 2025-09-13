package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroClienteRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Cliente;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.repository.ClienteRepository;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ArtesanoRepository artesanoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Cliente cliente;
    private Artesano artesano;
    private RegistroClienteRequest registroClienteRequest;
    private RegistroArtesanoRequest registroArtesanoRequest;

    @BeforeEach
    void setUp() {
        // Setup Cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Cliente");
        cliente.setEmail("cliente@test.com");
        cliente.setContraseña("encodedPassword");
        cliente.setRol(Usuario.Rol.CLIENTE);

        // Setup Artesano
        artesano = new Artesano();
        artesano.setId(2L);
        artesano.setNombre("María Artesana");
        artesano.setEmail("artesano@test.com");
        artesano.setContraseña("encodedPassword");
        artesano.setRol(Usuario.Rol.ARTESANO);
        artesano.setNombreEmprendimiento("Artesanías María");
        artesano.setDescripcion("Creaciones únicas");
        artesano.setUbicacion("Buenos Aires");

        // Setup RegistroClienteRequest
        registroClienteRequest = new RegistroClienteRequest();
        registroClienteRequest.setNombre("Juan Cliente");
        registroClienteRequest.setEmail("cliente@test.com");
        registroClienteRequest.setPassword("123456");

        // Setup RegistroArtesanoRequest
        registroArtesanoRequest = new RegistroArtesanoRequest();
        registroArtesanoRequest.setNombre("María Artesana");
        registroArtesanoRequest.setEmail("artesano@test.com");
        registroArtesanoRequest.setPassword("123456");
        registroArtesanoRequest.setNombreEmprendimiento("Artesanías María");
        registroArtesanoRequest.setDescripcion("Creaciones únicas");
        registroArtesanoRequest.setUbicacion("Buenos Aires");
    }

    @Test
    void buscarPorEmail_UsuarioExiste_DeberiaRetornarUsuario() {
        // Given
        String email = "cliente@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When
        Usuario resultado = usuarioService.buscarPorEmail(email);

        // Then
        assertNotNull(resultado);
        assertEquals(cliente.getEmail(), resultado.getEmail());
        assertEquals(cliente.getNombre(), resultado.getNombre());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void buscarPorEmail_UsuarioNoExiste_DeberiaLanzarExcepcion() {
        // Given
        String email = "noexiste@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.buscarPorEmail(email));
        
        assertEquals("Usuario no encontrado con email: " + email, exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void autenticar_CredencialesValidas_DeberiaRetornarUsuario() {
        // Given
        String email = "cliente@test.com";
        String password = "123456";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches(password, cliente.getContraseña())).thenReturn(true);

        // When
        Usuario resultado = usuarioService.autenticar(email, password);

        // Then
        assertNotNull(resultado);
        assertEquals(cliente.getEmail(), resultado.getEmail());
        verify(usuarioRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, cliente.getContraseña());
    }

    @Test
    void autenticar_ContraseñaIncorrecta_DeberiaLanzarExcepcion() {
        // Given
        String email = "cliente@test.com";
        String password = "passwordIncorrecta";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches(password, cliente.getContraseña())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.autenticar(email, password));
        
        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, cliente.getContraseña());
    }

    @Test
    void registrarCliente_EmailNoExiste_DeberiaCrearCliente() {
        // Given
        when(usuarioRepository.existsByEmail(registroClienteRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registroClienteRequest.getPassword())).thenReturn("encodedPassword");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        Cliente resultado = usuarioService.registrarCliente(registroClienteRequest);

        // Then
        assertNotNull(resultado);
        assertEquals(registroClienteRequest.getNombre(), resultado.getNombre());
        assertEquals(registroClienteRequest.getEmail(), resultado.getEmail());
        assertEquals(Usuario.Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository).existsByEmail(registroClienteRequest.getEmail());
        verify(passwordEncoder).encode(registroClienteRequest.getPassword());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void registrarCliente_EmailYaExiste_DeberiaLanzarExcepcion() {
        // Given
        when(usuarioRepository.existsByEmail(registroClienteRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.registrarCliente(registroClienteRequest));
        
        assertEquals("Ya existe un usuario con este email", exception.getMessage());
        verify(usuarioRepository).existsByEmail(registroClienteRequest.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void registrarArtesano_EmailNoExiste_DeberiaCrearArtesano() {
        // Given
        when(usuarioRepository.existsByEmail(registroArtesanoRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registroArtesanoRequest.getPassword())).thenReturn("encodedPassword");
        when(artesanoRepository.save(any(Artesano.class))).thenReturn(artesano);

        // When
        Artesano resultado = usuarioService.registrarArtesano(registroArtesanoRequest);

        // Then
        assertNotNull(resultado);
        assertEquals(registroArtesanoRequest.getNombre(), resultado.getNombre());
        assertEquals(registroArtesanoRequest.getEmail(), resultado.getEmail());
        assertEquals(Usuario.Rol.ARTESANO, resultado.getRol());
        assertEquals(registroArtesanoRequest.getNombreEmprendimiento(), resultado.getNombreEmprendimiento());
        verify(usuarioRepository).existsByEmail(registroArtesanoRequest.getEmail());
        verify(passwordEncoder).encode(registroArtesanoRequest.getPassword());
        verify(artesanoRepository).save(any(Artesano.class));
    }

    @Test
    void cambiarRol_ClienteAArtesano_DeberiaCambiarRol() {
        // Given
        String email = "cliente@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("ARTESANO");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        when(artesanoRepository.save(any(Artesano.class))).thenReturn(artesano);

        // When
        Usuario resultado = usuarioService.cambiarRol(email, request);

        // Then
        assertNotNull(resultado);
        assertEquals(Usuario.Rol.ARTESANO, resultado.getRol());
        verify(usuarioRepository).findByEmail(email);
        verify(artesanoRepository).save(any(Artesano.class));
    }

    @Test
    void cambiarRol_ArtesanoACliente_DeberiaCambiarRol() {
        // Given
        String email = "artesano@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("CLIENTE");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(artesano));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        Usuario resultado = usuarioService.cambiarRol(email, request);

        // Then
        assertNotNull(resultado);
        assertEquals(Usuario.Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository).findByEmail(email);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void cambiarRol_UsuarioAdmin_DeberiaLanzarExcepcion() {
        // Given
        Cliente admin = new Cliente();
        admin.setRol(Usuario.Rol.ADMIN);
        String email = "admin@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("CLIENTE");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(admin));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.cambiarRol(email, request));
        
        assertEquals("No se puede cambiar el rol de un administrador", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void cambiarRol_RolInvalido_DeberiaLanzarExcepcion() {
        // Given
        String email = "cliente@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("ROL_INVALIDO");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.cambiarRol(email, request));
        
        assertEquals("Rol inválido: ROL_INVALIDO", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void cambiarRol_MismoRol_DeberiaRetornarUsuarioSinCambios() {
        // Given
        String email = "cliente@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("CLIENTE");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When
        Usuario resultado = usuarioService.cambiarRol(email, request);

        // Then
        assertNotNull(resultado);
        assertEquals(Usuario.Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository).findByEmail(email);
        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(artesanoRepository, never()).save(any(Artesano.class));
    }
}