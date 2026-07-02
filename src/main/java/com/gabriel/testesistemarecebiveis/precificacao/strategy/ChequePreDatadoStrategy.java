package com.gabriel.testesistemarecebiveis.precificacao.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estratégia de spread para Cheque Pré-datado: 2,5% a.m.
 */
@Component
public class ChequePreDatadoStrategy implements SpreadStrategy {

    public static final String TIPO = "Cheque Pré-datado";
    private static final BigDecimal SPREAD = new BigDecimal("0.025");

    @Override
    public String tipoRecebivel() {
        return TIPO;
    }

    @Override
    public BigDecimal spread() {
        return SPREAD;
    }
}
