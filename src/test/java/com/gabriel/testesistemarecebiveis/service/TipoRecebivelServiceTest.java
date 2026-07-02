package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.TipoRecebivelRepository;
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
class TipoRecebivelServiceTest {

    @Mock
    private TipoRecebivelRepository tipoRecebivelRepository;

    @InjectMocks
    private TipoRecebivelService tipoRecebivelService;

    @Test
    void shouldCreateTipoRecebivelWhenNameIsProvided() {
        TipoRecebivel tipoRecebivel = TipoRecebivel.builder().nome("FUTURO").build();
        when(tipoRecebivelRepository.save(any(TipoRecebivel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TipoRecebivel created = tipoRecebivelService.create(tipoRecebivel);

        assertThat(created.getNome()).isEqualTo("FUTURO");
        verify(tipoRecebivelRepository).save(any(TipoRecebivel.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenNameIsBlank() {
        TipoRecebivel tipoRecebivel = TipoRecebivel.builder().nome(" ").build();

        assertThatThrownBy(() -> tipoRecebivelService.create(tipoRecebivel))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O nome do tipo de recebível é obrigatório.");
    }
}
