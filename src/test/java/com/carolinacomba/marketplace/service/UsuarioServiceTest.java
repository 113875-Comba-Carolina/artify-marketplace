package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.CambioRolRequest;
import com.carolinacomba.marketplace.dto.RegistroArtesanoRequest;
import com.carolinacomba.marketplace.dto.RegistroUsuarioRequest;
import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ArtesanoRepository artesanoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Artesano artesano;
    private RegistroUsuarioRequest registroUsuarioRequest;
    private RegistroArtesanoRequest registroArtesanoRequest;

    @BeforeEach
    void setUp() {
        // Setup Usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Usuario");
        usuario.setEmail("usuario@test.com");
        usuario.setContraseña("encodedPassword");
        usuario.setRol(Usuario.Rol.USUARIO);

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

        // Setup RegistroUsuarioRequest
        registroUsuarioRequest = new RegistroUsuarioRequest();
        registroUsuarioRequest.setNombre("Juan Usuario");
        registroUsuarioRequest.setEmail("usuario@test.com");
        registroUsuarioRequest.setPassword("123456");

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
        String email = "usuario@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorEmail(email);

        // Then
        assertNotNull(resultado);
        assertEquals(usuario.getEmail(), resultado.getEmail());
        assertEquals(usuario.getNombre(), resultado.getNombre());
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
        String email = "usuario@test.com";
        String password = "123456";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(password, usuario.getContraseña())).thenReturn(true);

        // When
        Usuario resultado = usuarioService.autenticar(email, password);

        // Then
        assertNotNull(resultado);
        assertEquals(usuario.getEmail(), resultado.getEmail());
        verify(usuarioRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, usuario.getContraseña());
    }

    @Test
    void autenticar_ContraseñaIncorrecta_DeberiaLanzarExcepcion() {
        // Given
        String email = "usuario@test.com";
        String password = "passwordIncorrecta";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(password, usuario.getContraseña())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.autenticar(email, password));
        
        assertEquals("Contraseña incorrecta", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, usuario.getContraseña());
    }

    @Test
    void registrarUsuario_EmailNoExiste_DeberiaCrearUsuario() {
        // Given
        when(usuarioRepository.existsByEmail(registroUsuarioRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registroUsuarioRequest.getPassword())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.registrarUsuario(registroUsuarioRequest);

        // Then
        assertNotNull(resultado);
        assertEquals(registroUsuarioRequest.getNombre(), resultado.getNombre());
        assertEquals(registroUsuarioRequest.getEmail(), resultado.getEmail());
        assertEquals(Usuario.Rol.USUARIO, resultado.getRol());
        verify(usuarioRepository).existsByEmail(registroUsuarioRequest.getEmail());
        verify(passwordEncoder).encode(registroUsuarioRequest.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_EmailYaExiste_DeberiaLanzarExcepcion() {
        // Given
        when(usuarioRepository.existsByEmail(registroUsuarioRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.registrarUsuario(registroUsuarioRequest));
        
        assertEquals("Ya existe un usuario con este email", exception.getMessage());
        verify(usuarioRepository).existsByEmail(registroUsuarioRequest.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
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
    void cambiarRol_UsuarioAUsuario_DeberiaRetornarUsuarioSinCambios() {
        // Given
        String email = "usuario@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("USUARIO");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.cambiarRol(email, request);

        // Then
        assertNotNull(resultado);
        assertEquals(Usuario.Rol.USUARIO, resultado.getRol());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void cambiarRol_UsuarioAdmin_DeberiaLanzarExcepcion() {
        // Given
        Usuario admin = new Usuario();
        admin.setRol(Usuario.Rol.ADMIN);
        String email = "admin@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("USUARIO");
        
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
        String email = "usuario@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("ROL_INVALIDO");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.cambiarRol(email, request));
        
        assertEquals("Rol inválido: ROL_INVALIDO", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void cambiarRol_UsuarioArtesano_DeberiaCambiarRol() {
        // Given
        String email = "usuario@test.com";
        CambioRolRequest request = new CambioRolRequest();
        request.setRol("ARTESANO");
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.cambiarRol(email, request);

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository).save(any(Usuario.class));
    }
}