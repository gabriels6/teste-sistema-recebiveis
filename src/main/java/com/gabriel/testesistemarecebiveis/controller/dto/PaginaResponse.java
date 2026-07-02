package com.gabriel.testesistemarecebiveis.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Envelope de paginação genérico, com o conteúdo da página e os metadados necessários para navegar
 * entre páginas.
 *
 * @param <T> tipo dos itens da página
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaResponse<T> {

    /** Itens da página atual. */
    private List<T> conteudo;

    /** Número da página atual (base zero). */
    private int pagina;

    /** Quantidade de itens por página. */
    private int tamanhoPagina;

    /** Total de itens que satisfazem o filtro (em todas as páginas). */
    private long totalElementos;

    /** Total de páginas disponíveis para o filtro. */
    private int totalPaginas;

    /**
     * Monta um envelope de paginação calculando o total de páginas a partir do total de elementos.
     *
     * @param conteudo       itens da página
     * @param pagina         número da página (base zero)
     * @param tamanhoPagina  itens por página
     * @param totalElementos total de itens no filtro
     * @param <T>            tipo dos itens
     * @return o envelope de paginação preenchido
     */
    public static <T> PaginaResponse<T> of(List<T> conteudo, int pagina, int tamanhoPagina,
                                           long totalElementos) {
        int totalPaginas = tamanhoPagina <= 0
                ? 0
                : (int) ((totalElementos + tamanhoPagina - 1) / tamanhoPagina);
        return PaginaResponse.<T>builder()
                .conteudo(conteudo)
                .pagina(pagina)
                .tamanhoPagina(tamanhoPagina)
                .totalElementos(totalElementos)
                .totalPaginas(totalPaginas)
                .build();
    }
}
