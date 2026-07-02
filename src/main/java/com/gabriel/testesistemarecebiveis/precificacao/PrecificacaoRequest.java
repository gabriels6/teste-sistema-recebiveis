package com.gabriel.testesistemarecebiveis.precificacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Dados de entrada para a precificação de um recebível.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrecificacaoRequest {

    /** Nome do tipo de recebível (ex.: "Duplicata Mercantil", "Cheque Pré-datado"). */
    private String tipoRecebivel;

    /** Valor de face (valor no vencimento). */
    private BigDecimal valorFace;

    /** Taxa base mensal, em decimal (ex.: 0.01 para 1% a.m.). */
    private BigDecimal taxaBase;

    /** Prazo em meses até o vencimento. */
    private Integer prazoMeses;
}
