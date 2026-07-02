package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.ChequePreDatadoStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.DuplicataMercantilStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import com.gabriel.testesistemarecebiveis.repositories.TaxaCambioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MotorPrecificacaoTest {

    private final SpreadStrategyResolver resolver = new SpreadStrategyResolver(
            List.of(new DuplicataMercantilStrategy(), new ChequePreDatadoStrategy()));
    private final TaxaCambioRepository taxaCambioRepository = Mockito.mock(TaxaCambioRepository.class);
    private final ConversorMoeda conversorMoeda = new ConversorMoeda(taxaCambioRepository);
    private final MotorPrecificacao motor = new MotorPrecificacao(resolver, conversorMoeda);

    @Test
    void shouldPriceDuplicataMercantilApplyingSpreadOf1_5Percent() {
        // VP = 1000 / (1 + 0.02 + 0.015)^3 = 1000 / 1.035^3 = 901,94
        ResultadoPrecificacao resultado = motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 3);

        assertThat(resultado.spread()).isEqualByComparingTo("0.015");
        assertThat(resultado.tipoRecebivel()).isEqualTo("Duplicata Mercantil");
        assertThat(resultado.valorPresente()).isEqualByComparingTo("901.94");
    }

    @Test
    void shouldPriceChequePreDatadoApplyingSpreadOf2_5Percent() {
        // VP = 1000 / (1 + 0.02 + 0.025)^3 = 1000 / 1.045^3 = 876,30
        ResultadoPrecificacao resultado = motor.precificar(
                "Cheque Pré-datado", new BigDecimal("1000"), new BigDecimal("0.02"), 3);

        assertThat(resultado.spread()).isEqualByComparingTo("0.025");
        assertThat(resultado.tipoRecebivel()).isEqualTo("Cheque Pré-datado");
        assertThat(resultado.valorPresente()).isEqualByComparingTo("876.30");
    }

    @Test
    void shouldReturnFaceValueWhenTermIsZero() {
        ResultadoPrecificacao resultado = motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 0);

        assertThat(resultado.valorPresente()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldThrowBusinessExceptionForUnknownTipoRecebivel() {
        assertThatThrownBy(() -> motor.precificar(
                "Nota Promissória", new BigDecimal("1000"), new BigDecimal("0.02"), 3))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não há estratégia de precificação para o tipo de recebível: Nota Promissória.");
    }

    @Test
    void shouldThrowBusinessExceptionWhenValorFaceIsNotPositive() {
        assertThatThrownBy(() -> motor.precificar(
                "Duplicata Mercantil", BigDecimal.ZERO, new BigDecimal("0.02"), 3))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O valor de face deve ser maior que zero.");
    }

    @Test
    void shouldThrowBusinessExceptionWhenTaxaBaseIsNegative() {
        assertThatThrownBy(() -> motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("-0.01"), 3))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A taxa base deve ser maior ou igual a zero.");
    }

    @Test
    void shouldConvertPresentValueUsingExchangeRateWhenCurrencyDiffers() {
        LocalDate data = LocalDate.of(2026, 7, 2);
        // VP em BRL = 1000 / 1.035^3 = 901,9426... ; taxa BRL->USD = 0.20 => 180,39
        Mockito.when(taxaCambioRepository
                        .findByMoedaOrigem_CodMoedaIgnoreCaseAndMoedaDestino_CodMoedaIgnoreCaseAndDataReferencia(
                                "BRL", "USD", data))
                .thenReturn(Optional.of(TaxaCambio.builder().valor(new BigDecimal("0.20")).build()));

        ResultadoPrecificacao resultado = motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 3,
                "BRL", "USD", data);

        assertThat(resultado.moeda()).isEqualTo("USD");
        assertThat(resultado.taxaCambio()).isEqualByComparingTo("0.20");
        assertThat(resultado.valorPresente()).isEqualByComparingTo("180.39");
    }

    @Test
    void shouldMultiplyByOneAndSkipLookupWhenCurrencyIsTheSame() {
        ResultadoPrecificacao resultado = motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 3,
                "BRL", "brl", null);

        assertThat(resultado.moeda()).isEqualTo("brl");
        assertThat(resultado.taxaCambio()).isEqualByComparingTo("1");
        assertThat(resultado.valorPresente()).isEqualByComparingTo("901.94");
        Mockito.verifyNoInteractions(taxaCambioRepository);
    }

    @Test
    void shouldThrowExplanatoryBusinessExceptionWhenExchangeRateIsMissing() {
        LocalDate data = LocalDate.of(2026, 7, 2);
        Mockito.when(taxaCambioRepository
                        .findByMoedaOrigem_CodMoedaIgnoreCaseAndMoedaDestino_CodMoedaIgnoreCaseAndDataReferencia(
                                "BRL", "USD", data))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 3,
                "BRL", "USD", data))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não há taxa de câmbio cadastrada de BRL para USD na data 2026-07-02.");
    }

    @Test
    void shouldThrowBusinessExceptionWhenConvertingWithoutDate() {
        assertThatThrownBy(() -> motor.precificar(
                "Duplicata Mercantil", new BigDecimal("1000"), new BigDecimal("0.02"), 3,
                "BRL", "USD", null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A data para conversão de câmbio é obrigatória.");
    }
}
