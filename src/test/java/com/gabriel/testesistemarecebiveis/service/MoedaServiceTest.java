package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
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
class MoedaServiceTest {

    @Mock
    private MoedaRepository moedaRepository;

    @InjectMocks
    private MoedaService moedaService;

    @Test
    void shouldCreateMoedaWhenCodeIsProvided() {
        Moeda moeda = Moeda.builder().codMoeda("BRL").build();
        when(moedaRepository.save(any(Moeda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Moeda created = moedaService.create(moeda);

        assertThat(created.getCodMoeda()).isEqualTo("BRL");
        verify(moedaRepository).save(any(Moeda.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCodeIsBlank() {
        Moeda moeda = Moeda.builder().codMoeda(" ").build();

        assertThatThrownBy(() -> moedaService.create(moeda))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O código da moeda é obrigatório.");
    }
}
