package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FuncaoRepository funcaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void shouldCreateUsuarioWhenDataIsValid() {
        Funcao funcao = Funcao.builder().id(1).nome("ADMIN").build();
        Usuario usuario = Usuario.builder().nome("gabriel").hashSenha("senha123").funcao(funcao).build();
        when(funcaoRepository.findById(1)).thenReturn(Optional.of(funcao));
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario created = usuarioService.create(usuario);

        assertThat(created.getNome()).isEqualTo("gabriel");
        assertThat(created.getHashSenha()).isEqualTo("encoded");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenFuncaoIsMissing() {
        Usuario usuario = Usuario.builder().nome("gabriel").hashSenha("senha123").build();

        assertThatThrownBy(() -> usuarioService.create(usuario))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A função do usuário é obrigatória.");
    }
}
