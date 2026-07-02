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
 * @param valorPresente valor presente calculado
 */
public record ResultadoPrecificacao(
        String tipoRecebivel,
        BigDecimal valorFace,
        BigDecimal taxaBase,
        BigDecimal spread,
        int prazoMeses,
        BigDecimal valorPresente) {
}
