package com.gabriel.testesistemarecebiveis.precificacao.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estratégia de spread para Duplicata Mercantil: 1,5% a.m.
 */
@Component
public class DuplicataMercantilStrategy implements SpreadStrategy {

    public static final String TIPO = "Duplicata Mercantil";
    private static final BigDecimal SPREAD = new BigDecimal("0.015");

    @Override
    public String tipoRecebivel() {
        return TIPO;
    }

    @Override
    public BigDecimal spread() {
        return SPREAD;
    }
}
