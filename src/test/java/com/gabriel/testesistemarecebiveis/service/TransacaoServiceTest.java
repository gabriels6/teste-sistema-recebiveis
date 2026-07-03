package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.controller.dto.DesagioResponse;
import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.Recebivel;
import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.entities.Transacao;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.precificacao.MotorPrecificacao;
import com.gabriel.testesistemarecebiveis.precificacao.ResultadoPrecificacao;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private MotorPrecificacao motorPrecificacao;

    @InjectMocks
    private TransacaoService transacaoService;

    /** Recebível de apoio para os testes de deságio (tipo, taxa base, moeda e vencimento). */
    private Recebivel recebivelExemplo(String moedaCodigo) {
        return Recebivel.builder()
                .id(5)
                .tipoRecebivel(TipoRecebivel.builder().nome("Duplicata Mercantil").build())
                .taxaBase(new BigDecimal("0.01000000"))
                .moeda(Moeda.builder().codMoeda(moedaCodigo).build())
                .dataVencimento(LocalDate.of(2026, 12, 1))
                .build();
    }

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

    @Test
    void shouldComputeDesagioFromPresentValueAndUnitPrice() {
        Recebivel recebivel = recebivelExemplo("BRL");
        Moeda moeda = Moeda.builder().id(3).codMoeda("BRL").build();
        when(recebivelRepository.findById(5)).thenReturn(Optional.of(recebivel));
        when(moedaRepository.findById(3)).thenReturn(Optional.of(moeda));
        // Valor presente = 110; preço unitário = 100 => deságio = 110/100 - 1 = 0.10.
        when(motorPrecificacao.precificar(anyString(), any(), any(), anyInt(), any(), any(), any()))
                .thenReturn(new ResultadoPrecificacao("Duplicata Mercantil", new BigDecimal("100"),
                        new BigDecimal("0.01"), BigDecimal.ZERO, 5, new BigDecimal("110.00"),
                        "BRL", BigDecimal.ONE));

        Transacao transacao = Transacao.builder()
                .recebivel(Recebivel.builder().id(5).build())
                .moeda(Moeda.builder().id(3).build())
                .dataOperacao(LocalDate.of(2026, 7, 2))
                .qtdeOperacao(new BigDecimal("100"))
                .precoUnitario(new BigDecimal("100"))
                .build();

        DesagioResponse response = transacaoService.calcularDesagio(transacao);

        assertThat(response.valorPresente()).isEqualByComparingTo("110.00");
        assertThat(response.precoUnitario()).isEqualByComparingTo("100");
        assertThat(response.desagio()).isEqualByComparingTo("0.10");
        assertThat(response.moeda()).isEqualTo("BRL");
    }

    @Test
    void shouldThrowWhenUnitPriceMissingForDesagio() {
        Transacao transacao = Transacao.builder()
                .recebivel(Recebivel.builder().id(5).build())
                .moeda(Moeda.builder().id(3).build())
                .dataOperacao(LocalDate.of(2026, 7, 2))
                .qtdeOperacao(new BigDecimal("100"))
                .build();

        assertThatThrownBy(() -> transacaoService.calcularDesagio(transacao))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O preço unitário deve ser maior que zero.");
    }

    @Test
    void shouldFillDesagioWhenListing() {
        Transacao transacao = Transacao.builder()
                .id(1)
                .recebivel(recebivelExemplo("BRL"))
                .moeda(Moeda.builder().id(3).codMoeda("BRL").build())
                .dataOperacao(LocalDate.of(2026, 7, 2))
                .qtdeOperacao(new BigDecimal("100"))
                .precoUnitario(new BigDecimal("100"))
                .build();
        when(transacaoRepository.findAll()).thenReturn(List.of(transacao));
        when(motorPrecificacao.precificar(anyString(), any(), any(), anyInt(), any(), any(), any()))
                .thenReturn(new ResultadoPrecificacao("Duplicata Mercantil", new BigDecimal("100"),
                        new BigDecimal("0.01"), BigDecimal.ZERO, 5, new BigDecimal("110.00"),
                        "BRL", BigDecimal.ONE));

        List<Transacao> transacoes = transacaoService.findAll();

        assertThat(transacoes).hasSize(1);
        assertThat(transacoes.get(0).getDesagio()).isEqualByComparingTo("0.10");
    }

    @Test
    void shouldNotBreakListingWhenPricingFails() {
        Transacao transacao = Transacao.builder()
                .id(1)
                .recebivel(recebivelExemplo("USD"))
                .moeda(Moeda.builder().id(3).codMoeda("BRL").build())
                .dataOperacao(LocalDate.of(2026, 7, 2))
                .qtdeOperacao(new BigDecimal("100"))
                .precoUnitario(new BigDecimal("100"))
                .build();
        when(transacaoRepository.findAll()).thenReturn(List.of(transacao));
        // Sem taxa de câmbio cadastrada USD->BRL: a precificação falha, mas a listagem segue.
        when(motorPrecificacao.precificar(anyString(), any(), any(), anyInt(), any(), any(), any()))
                .thenThrow(new BusinessException("Não há taxa de câmbio cadastrada."));

        List<Transacao> transacoes = transacaoService.findAll();

        assertThat(transacoes).hasSize(1);
        assertThat(transacoes.get(0).getDesagio()).isNull();
    }
}
