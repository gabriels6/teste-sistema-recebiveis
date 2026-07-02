package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

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
 *
 * <p>Ao final do cálculo, o valor presente é convertido para a moeda alvo informada como parâmetro
 * utilizando a taxa de câmbio da data. Quando a moeda alvo é a mesma do recebível (ou não é
 * informada), o fator de conversão é 1 — preservando um único fluxo de cálculo.</p>
 */
@Service
public class MotorPrecificacao {

    /** Escala (casas decimais) do valor presente resultante. */
    private static final int ESCALA_MONETARIA = 2;

    /** Precisão usada nos cálculos intermediários para preservar exatidão antes do arredondamento. */
    private static final MathContext PRECISAO = MathContext.DECIMAL64;

    private final SpreadStrategyResolver strategyResolver;
    private final ConversorMoeda conversorMoeda;

    public MotorPrecificacao(SpreadStrategyResolver strategyResolver, ConversorMoeda conversorMoeda) {
        this.strategyResolver = strategyResolver;
        this.conversorMoeda = conversorMoeda;
    }

    /**
     * Calcula o valor presente resolvendo a estratégia de spread pelo tipo de recebível, sem
     * conversão de moeda.
     *
     * @param tipoRecebivel nome do tipo de recebível (ex.: "Duplicata Mercantil")
     * @param valorFace     valor de face (valor no vencimento)
     * @param taxaBase      taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses    prazo em meses até o vencimento
     */
    public ResultadoPrecificacao precificar(String tipoRecebivel, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses) {
        return precificar(tipoRecebivel, valorFace, taxaBase, prazoMeses, null, null, null);
    }

    /**
     * Calcula o valor presente resolvendo a estratégia de spread pelo tipo de recebível e,
     * ao final, converte o resultado para a moeda alvo informada.
     *
     * @param tipoRecebivel  nome do tipo de recebível (ex.: "Duplicata Mercantil")
     * @param valorFace      valor de face (valor no vencimento)
     * @param taxaBase       taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses     prazo em meses até o vencimento
     * @param moedaRecebivel código da moeda do recebível (ex.: "BRL")
     * @param moedaAlvo      código da moeda alvo informada como parâmetro; quando nula, em branco
     *                       ou igual à moeda do recebível, não há conversão
     * @param dataCambio     data de referência para a busca da taxa de câmbio
     */
    public ResultadoPrecificacao precificar(String tipoRecebivel, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses,
                                            String moedaRecebivel, String moedaAlvo,
                                            LocalDate dataCambio) {
        SpreadStrategy strategy = strategyResolver.resolver(tipoRecebivel);
        return precificar(strategy, valorFace, taxaBase, prazoMeses, moedaRecebivel, moedaAlvo,
                dataCambio);
    }

    /**
     * Calcula o valor presente utilizando diretamente a estratégia de spread informada, sem
     * conversão de moeda.
     *
     * @param strategy   estratégia que fornece o spread a ser aplicado
     * @param valorFace  valor de face (valor no vencimento)
     * @param taxaBase   taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses prazo em meses até o vencimento
     */
    public ResultadoPrecificacao precificar(SpreadStrategy strategy, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses) {
        return precificar(strategy, valorFace, taxaBase, prazoMeses, null, null, null);
    }

    /**
     * Calcula o valor presente utilizando diretamente a estratégia de spread informada e,
     * ao final, converte o resultado para a moeda alvo informada.
     *
     * @param strategy       estratégia que fornece o spread a ser aplicado
     * @param valorFace      valor de face (valor no vencimento)
     * @param taxaBase       taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.)
     * @param prazoMeses     prazo em meses até o vencimento
     * @param moedaRecebivel código da moeda do recebível (ex.: "BRL")
     * @param moedaAlvo      código da moeda alvo informada como parâmetro; quando nula, em branco
     *                       ou igual à moeda do recebível, não há conversão
     * @param dataCambio     data de referência para a busca da taxa de câmbio
     */
    public ResultadoPrecificacao precificar(SpreadStrategy strategy, BigDecimal valorFace,
                                            BigDecimal taxaBase, int prazoMeses,
                                            String moedaRecebivel, String moedaAlvo,
                                            LocalDate dataCambio) {
        validar(valorFace, taxaBase, prazoMeses);

        BigDecimal spread = strategy.spread();

        // Fator de desconto: (1 + Taxa Base + Spread) ^ Prazo
        BigDecimal taxaEfetiva = BigDecimal.ONE.add(taxaBase).add(spread);
        BigDecimal fatorDesconto = taxaEfetiva.pow(prazoMeses, PRECISAO);

        // Valor Presente = Valor Face / fator de desconto (na moeda do recebível)
        BigDecimal valorPresente = valorFace.divide(fatorDesconto, PRECISAO);

        // Conversão de moeda ao final do cálculo. O fator é 1 quando não há conversão a fazer,
        // mantendo um único fluxo de cálculo.
        BigDecimal fatorCambio = conversorMoeda.fatorConversao(moedaRecebivel, moedaAlvo, dataCambio);
        BigDecimal valorConvertido = valorPresente
                .multiply(fatorCambio, PRECISAO)
                .setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);

        String moedaResultado = moedaResultado(moedaRecebivel, moedaAlvo);

        return new ResultadoPrecificacao(strategy.tipoRecebivel(), valorFace, taxaBase, spread,
                prazoMeses, valorConvertido, moedaResultado, fatorCambio);
    }

    /**
     * Determina o código da moeda do resultado: a moeda alvo quando informada, caso contrário a
     * moeda do recebível.
     */
    private String moedaResultado(String moedaRecebivel, String moedaAlvo) {
        if (moedaAlvo != null && !moedaAlvo.isBlank()) {
            return moedaAlvo.trim();
        }
        return (moedaRecebivel == null || moedaRecebivel.isBlank()) ? null : moedaRecebivel.trim();
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
