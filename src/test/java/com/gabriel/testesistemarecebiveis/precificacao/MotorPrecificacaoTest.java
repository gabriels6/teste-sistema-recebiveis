package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.ChequePreDatadoStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.DuplicataMercantilStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MotorPrecificacaoTest {

    private final SpreadStrategyResolver resolver = new SpreadStrategyResolver(
            List.of(new DuplicataMercantilStrategy(), new ChequePreDatadoStrategy()));
    private final MotorPrecificacao motor = new MotorPrecificacao(resolver);

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
}
