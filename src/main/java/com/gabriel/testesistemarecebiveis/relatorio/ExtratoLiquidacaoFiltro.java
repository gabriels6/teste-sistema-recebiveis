package com.gabriel.testesistemarecebiveis.relatorio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Critérios de filtragem e paginação do relatório de Extrato de Liquidação.
 *
 * <p>Todos os filtros são opcionais e combináveis: é possível filtrar apenas por range de data de
 * operação, apenas por range de data de liquidação, por ambos, e ainda restringir por nome do
 * cedente (correspondência parcial) e/ou moeda da operação.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtratoLiquidacaoFiltro {

    /** Início (inclusive) do range de data de operação. */
    private LocalDate dataOperacaoInicio;

    /** Fim (inclusive) do range de data de operação. */
    private LocalDate dataOperacaoFim;

    /** Início (inclusive) do range de data de liquidação. */
    private LocalDate dataLiquidacaoInicio;

    /** Fim (inclusive) do range de data de liquidação. */
    private LocalDate dataLiquidacaoFim;

    /** Nome (ou parte do nome) do cedente; correspondência parcial, sem distinção de caixa. */
    private String nomeCedente;

    /** Código da moeda da operação (ex.: "BRL", "USD"). */
    private String moeda;

    /** Página solicitada, base zero. */
    private int pagina;

    /** Quantidade de itens por página. */
    private int tamanhoPagina;
}
