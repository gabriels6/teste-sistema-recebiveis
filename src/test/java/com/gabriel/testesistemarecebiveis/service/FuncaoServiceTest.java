package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncaoServiceTest {

    @Mock
    private FuncaoRepository funcaoRepository;

    @InjectMocks
    private FuncaoService funcaoService;

    @Test
    void shouldCreateFuncaoWhenDataIsValid() {
        Funcao funcao = Funcao.builder().nome("ADMIN").build();
        when(funcaoRepository.save(any(Funcao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcao created = funcaoService.create(funcao);

        assertThat(created.getNome()).isEqualTo("ADMIN");
        verify(funcaoRepository).save(any(Funcao.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenNameIsBlank() {
        Funcao funcao = Funcao.builder().nome(" ").build();

        assertThatThrownBy(() -> funcaoService.create(funcao))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O nome da função é obrigatório.");
    }
}
