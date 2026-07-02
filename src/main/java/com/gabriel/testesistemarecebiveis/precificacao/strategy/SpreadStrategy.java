package com.gabriel.testesistemarecebiveis.precificacao.strategy;

import java.math.BigDecimal;

/**
 * Estratégia de precificação (Strategy Pattern).
 *
 * <p>O cálculo do Valor Presente é concentrado no
 * {@link com.gabriel.testesistemarecebiveis.precificacao.MotorPrecificacao}. A única variação entre
 * os diferentes tipos de recebível é o <em>spread</em> aplicado, portanto cada estratégia concreta
 * apenas informa qual spread mensal usar e para qual tipo de recebível ela é responsável.</p>
 */
public interface SpreadStrategy {

    /**
     * Nome do tipo de recebível suportado por esta estratégia (ex.: "Duplicata Mercantil").
     */
    String tipoRecebivel();

    /**
     * Spread mensal, em forma decimal (ex.: 0.015 para 1,5% a.m.).
     */
    BigDecimal spread();
}
