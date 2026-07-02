package com.gabriel.testesistemarecebiveis.precificacao;

import java.math.BigDecimal;

/**
 * Resultado da precificação de um recebível, com o detalhamento dos parâmetros utilizados no cálculo
 * do valor presente.
 *
 * @param tipoRecebivel tipo de recebível precificado
 * @param valorFace     valor de face (valor no vencimento)
 * @param taxaBase      taxa base mensal aplicada, em decimal
 * @param spread        spread mensal aplicado pela estratégia, em decimal
 * @param prazoMeses    prazo em meses até o vencimento
 * @param valorPresente valor presente calculado, já convertido para a moeda de resultado
 * @param moeda         código da moeda do valor presente resultante (nula quando não informada)
 * @param taxaCambio    fator de câmbio aplicado ao final do cálculo (1 quando não há conversão)
 */
public record ResultadoPrecificacao(
        String tipoRecebivel,
        BigDecimal valorFace,
        BigDecimal taxaBase,
        BigDecimal spread,
        int prazoMeses,
        BigDecimal valorPresente,
        String moeda,
        BigDecimal taxaCambio) {
}
