package com.gabriel.testesistemarecebiveis.ptax;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapeia a resposta OData do servico PTAX. Apenas os campos utilizados sao
 * declarados; os demais (ex.: {@code @odata.context}, paridades) sao ignorados.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PtaxCotacaoResponse(List<Cotacao> value) {

    /**
     * Uma cotacao diaria. {@code cotacaoVenda} e o valor de 1 unidade da moeda
     * estrangeira em Reais no fechamento PTAX.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cotacao(
            BigDecimal cotacaoCompra,
            BigDecimal cotacaoVenda,
            String dataHoraCotacao,
            String tipoBoletim) {
    }
}
