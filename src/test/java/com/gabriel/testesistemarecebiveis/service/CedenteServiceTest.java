package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.CedenteRepository;
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
class CedenteServiceTest {

    @Mock
    private CedenteRepository cedenteRepository;

    @InjectMocks
    private CedenteService cedenteService;

    @Test
    void shouldCreateCedenteWhenDataIsValid() {
        Cedente cedente = Cedente.builder().codEmpresa("EMP001").nome("Empresa A").build();
        when(cedenteRepository.save(any(Cedente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cedente created = cedenteService.create(cedente);

        assertThat(created.getNome()).isEqualTo("Empresa A");
        verify(cedenteRepository).save(any(Cedente.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCodeIsBlank() {
        Cedente cedente = Cedente.builder().codEmpresa(" ").nome("Empresa A").build();

        assertThatThrownBy(() -> cedenteService.create(cedente))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O código da empresa é obrigatório.");
    }
}
