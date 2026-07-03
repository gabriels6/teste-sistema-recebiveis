package com.gabriel.testesistemarecebiveis.controller.dto;

import java.math.BigDecimal;

/**
 * Resultado do cálculo de deságio de uma transação.
 *
 * <p>O deságio compara o valor presente do recebível (calculado pelo motor de
 * precificação) com o preço unitário pago na operação:</p>
 *
 * <pre>
 *     deságio = valor presente / preço unitário - 1
 * </pre>
 *
 * @param valorPresente valor presente do recebível, já na moeda da operação
 * @param precoUnitario preço unitário informado na transação
 * @param desagio       deságio resultante (positivo = compra abaixo do valor presente)
 * @param moeda         código da moeda do valor presente resultante
 */
public record DesagioResponse(
        BigDecimal valorPresente,
        BigDecimal precoUnitario,
        BigDecimal desagio,
        String moeda) {
}
