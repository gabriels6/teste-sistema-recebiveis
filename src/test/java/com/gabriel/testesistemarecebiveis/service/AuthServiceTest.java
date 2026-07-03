package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.controller.dto.AuthResponse;
import com.gabriel.testesistemarecebiveis.controller.dto.LoginRequest;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.exception.InvalidCredentialsException;
import com.gabriel.testesistemarecebiveis.exception.UserBlockedException;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import com.gabriel.testesistemarecebiveis.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);
    }

    @Test
    void shouldAuthenticateUserAndReturnTokenWhenCredentialsAreValid() {
        String rawPassword = "senha123";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        Usuario usuario = Usuario.builder()
                .id(1)
                .nome("usuario")
                .hashSenha(hashedPassword)
                .tentativas(2)
                .build();

        when(usuarioRepository.findByNome("usuario")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("token123");

        AuthResponse response = authService.authenticate(new LoginRequest("usuario", rawPassword));

        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getMessage()).isEqualTo("Login efetuado com sucesso.");
        assertThat(response.getRemainingAttempts()).isEqualTo(3);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getTentativas()).isEqualTo(0);
    }

    @Test
    void shouldIncrementAttemptsAndReturnRemainingWhenPasswordIsInvalid() {
        Usuario usuario = Usuario.builder()
                .id(2)
                .nome("usuario")
                .hashSenha(passwordEncoder.encode("senhaCorreta"))
                .tentativas(0)
                .build();

        when(usuarioRepository.findByNome("usuario")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("usuario", "senhaErrada")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Nome de usuário ou senha inválida.");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getTentativas()).isEqualTo(1);
    }

    @Test
    void shouldBlockUserAfterThreeFailedAttempts() {
        Usuario usuario = Usuario.builder()
                .id(3)
                .nome("usuario")
                .hashSenha(passwordEncoder.encode("senhaCorreta"))
                .tentativas(2)
                .build();

        when(usuarioRepository.findByNome("usuario")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("usuario", "senhaErrada")))
                .isInstanceOf(UserBlockedException.class)
                .hasMessage("Usuário bloqueado após 3 tentativas falhas.");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getTentativas()).isEqualTo(3);
    }

    @Test
    void shouldReturnUnauthorizedWhenUsernameDoesNotExist() {
        when(usuarioRepository.findByNome("usuarioInexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("usuarioInexistente", "senha")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Nome de usuário ou senha inválida.");
    }
}
