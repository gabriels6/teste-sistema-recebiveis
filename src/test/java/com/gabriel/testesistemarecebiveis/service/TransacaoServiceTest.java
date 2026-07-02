package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Transacao;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.RecebivelRepository;
import com.gabriel.testesistemarecebiveis.repositories.TransacaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecebivelRepository recebivelRepository;

    @Mock
    private MoedaRepository moedaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    @Test
    void shouldSettleTransactionFillingProvidedDate() {
        LocalDate dataLiquidacao = LocalDate.of(2026, 7, 2);
        Transacao existing = Transacao.builder().id(1).build();
        when(transacaoRepository.findById(1)).thenReturn(Optional.of(existing));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Transacao liquidada = transacaoService.liquidar(1, dataLiquidacao);

        assertThat(liquidada.getDataLiquidacao()).isEqualTo(dataLiquidacao);
        verify(transacaoRepository).save(existing);
    }

    @Test
    void shouldThrowWhenSettlementDateIsMissing() {
        assertThatThrownBy(() -> transacaoService.liquidar(1, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A data de liquidação é obrigatória.");
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTransactionAlreadySettled() {
        Transacao existing = Transacao.builder().id(1).dataLiquidacao(LocalDate.of(2026, 1, 1)).build();
        when(transacaoRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> transacaoService.liquidar(1, LocalDate.of(2026, 7, 2)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A transação já está liquidada.");
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenTransactionDoesNotExist() {
        when(transacaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService.liquidar(99, LocalDate.of(2026, 7, 2)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldPropagateOptimisticLockFailureOnConcurrentModification() {
        Transacao existing = Transacao.builder().id(1).build();
        when(transacaoRepository.findById(1)).thenReturn(Optional.of(existing));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Transacao.class, 1));

        assertThatThrownBy(() -> transacaoService.liquidar(1, LocalDate.of(2026, 7, 2)))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
