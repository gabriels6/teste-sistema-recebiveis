package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Motor de precificação de recebíveis.
 *
 * <p>Concentra o cálculo do Valor Presente segundo a fórmula:</p>
 *
 * <pre>
 *     Valor Presente = Valor Face / (1 + Taxa Base + Spread) ^ Prazo
 * </pre>
 *
 * <p>A única variação entre os tipos de recebível é o spread, fornecido por uma
 * {@link SpreadStrategy} (Strategy Pattern). O motor não conhece os tipos concretos: ele resolve a
 * estratégia adequada através do {@link SpreadStrategyResolver} e aplica sempre a mesma fórmula.</p>
 */
@Service
public class MotorPrecificacao {

    /** Escala (casas decimais) do valor presente resultante. */
    private static final int ESCALA_MONETARIA = 2;

    /** Precisão usada nos cálculos intermediários para preservar exatidão antes do arredondamento. */
    private static final MathContext PRECISAO = MathContext.DECIMAL64;

    private final SpreadStrategyResolver strategyResolver;

    public MotorPrecificacao(SpreadStrategyResolver strategyResolver) {
        this.strategyResolver = strategyResolver;
    }

    /**
     * Calcula o valor presente resolvendo a estratégia de spread pelo tipo de recebível.
     *
     * @param tipoRecebivel nome do tipo de recebível (ex.: "Duplicata Mercantil")
     * @param valorFace     valor de face (valor no vencimento)
     * @param taxaBase      taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses    prazo em meses até o vencimento
     */
    public ResultadoPrecificacao precificar(String tipoRecebivel, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses) {
        SpreadStrategy strategy = strategyResolver.resolver(tipoRecebivel);
        return precificar(strategy, valorFace, taxaBase, prazoMeses);
    }

    /**
     * Calcula o valor presente utilizando diretamente a estratégia de spread informada.
     *
     * @param strategy   estratégia que fornece o spread a ser aplicado
     * @param valorFace  valor de face (valor no vencimento)
     * @param taxaBase   taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses prazo em meses até o vencimento
     */
    public ResultadoPrecificacao precificar(SpreadStrategy strategy, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses) {
        validar(valorFace, taxaBase, prazoMeses);

        BigDecimal spread = strategy.spread();

        // Fator de desconto: (1 + Taxa Base + Spread) ^ Prazo
        BigDecimal taxaEfetiva = BigDecimal.ONE.add(taxaBase).add(spread);
        BigDecimal fatorDesconto = taxaEfetiva.pow(prazoMeses, PRECISAO);

        // Valor Presente = Valor Face / fator de desconto
        BigDecimal valorPresente = valorFace
                .divide(fatorDesconto, PRECISAO)
                .setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);

        return new ResultadoPrecificacao(strategy.tipoRecebivel(), valorFace, taxaBase, spread,
                prazoMeses, valorPresente);
    }

    private void validar(BigDecimal valorFace, BigDecimal taxaBase, int prazoMeses) {
        if (valorFace == null || valorFace.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor de face deve ser maior que zero.");
        }
        if (taxaBase == null || taxaBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("A taxa base deve ser maior ou igual a zero.");
        }
        if (prazoMeses < 0) {
            throw new BusinessException("O prazo em meses deve ser maior ou igual a zero.");
        }
    }
}
