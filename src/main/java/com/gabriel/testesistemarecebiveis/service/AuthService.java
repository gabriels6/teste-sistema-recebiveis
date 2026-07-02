package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.controller.dto.AuthResponse;
import com.gabriel.testesistemarecebiveis.controller.dto.LoginRequest;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.exception.InvalidCredentialsException;
import com.gabriel.testesistemarecebiveis.exception.UserBlockedException;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import com.gabriel.testesistemarecebiveis.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final int MAX_ATTEMPTS = 3;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse authenticate(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByNome(request.getNome())
                .orElseThrow(() -> new InvalidCredentialsException("Nome de usuário ou senha inválida.", MAX_ATTEMPTS - 1));

        Integer currentAttempts = usuario.getTentativas() == null ? 0 : usuario.getTentativas();
        if (currentAttempts >= MAX_ATTEMPTS) {
            throw new UserBlockedException("Usuário bloqueado após 3 tentativas falhas. Contate o administrador.", 0);
        }

        boolean passwordMatches = passwordEncoder.matches(request.getSenha(), usuario.getHashSenha());
        if (!passwordMatches) {
            int updatedAttempts = currentAttempts + 1;
            usuario.setTentativas(updatedAttempts);
            usuarioRepository.save(usuario);
            int remainingAttempts = Math.max(0, MAX_ATTEMPTS - updatedAttempts);
            if (updatedAttempts >= MAX_ATTEMPTS) {
                throw new UserBlockedException("Usuário bloqueado após 3 tentativas falhas.", remainingAttempts);
            }
            throw new InvalidCredentialsException("Nome de usuário ou senha inválida.", remainingAttempts);
        }

        usuario.setTentativas(0);
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .message("Login efetuado com sucesso.")
                .remainingAttempts(MAX_ATTEMPTS)
                .build();
    }
}
