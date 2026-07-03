package com.gabriel.testesistemarecebiveis.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Linha do relatório de Extrato de Liquidação, reunindo dados da transação, do recebível utilizado no
 * cálculo e do cedente.
 *
 * @param transacaoId        identificador da transação
 * @param dataOperacao       data em que a operação foi realizada
 * @param dataLiquidacao     data de liquidação (nula quando ainda não liquidada)
 * @param valorFace          valor de face da operação (quantidade operada, na moeda da operação)
 * @param moedaOperacao      código da moeda da operação/liquidação
 * @param codAtivo           código do ativo (recebível) negociado
 * @param tipoRecebivel      tipo do recebível (ex.: "Duplicata Mercantil")
 * @param taxaBase           taxa base mensal do recebível, em decimal
 * @param spread             spread mensal aplicado ao tipo de recebível, em decimal (nulo quando não
 *                           há estratégia de precificação cadastrada para o tipo)
 * @param moedaRecebivel     código da moeda em que o recebível é denominado
 * @param cedenteNome        nome do cedente
 * @param cedenteCodEmpresa  código da empresa cedente
 */
public record ExtratoLiquidacaoItem(
        Integer transacaoId,
        LocalDate dataOperacao,
        LocalDate dataLiquidacao,
        BigDecimal valorFace,
        String moedaOperacao,
        String codAtivo,
        String tipoRecebivel,
        BigDecimal taxaBase,
        BigDecimal spread,
        String moedaRecebivel,
        String cedenteNome,
        String cedenteCodEmpresa) {

    /**
     * Retorna uma cópia desta linha com o spread informado, preservando os demais campos.
     * O spread não é persistido: ele é derivado do tipo de recebível pela camada de serviço.
     *
     * @param novoSpread spread mensal a aplicar
     * @return nova linha com o spread preenchido
     */
    public ExtratoLiquidacaoItem comSpread(BigDecimal novoSpread) {
        return new ExtratoLiquidacaoItem(transacaoId, dataOperacao, dataLiquidacao, valorFace,
                moedaOperacao, codAtivo, tipoRecebivel, taxaBase, novoSpread, moedaRecebivel,
                cedenteNome, cedenteCodEmpresa);
    }
}
