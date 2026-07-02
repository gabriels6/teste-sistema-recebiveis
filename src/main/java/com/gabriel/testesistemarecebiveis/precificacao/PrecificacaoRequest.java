package com.gabriel.testesistemarecebiveis.precificacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    /** Código da moeda do recebível (ex.: "BRL"). */
    private String moedaRecebivel;

    /**
     * Código da moeda alvo para o resultado (ex.: "USD"). Quando não informada ou igual à moeda do
     * recebível, o valor presente é mantido na moeda do recebível (sem conversão).
     */
    private String moeda;

    /** Data de referência usada para buscar a taxa de câmbio quando há conversão de moeda. */
    private LocalDate dataCambio;
}
