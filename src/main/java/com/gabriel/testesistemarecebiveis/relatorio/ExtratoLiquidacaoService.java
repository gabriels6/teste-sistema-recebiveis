package com.gabriel.testesistemarecebiveis.relatorio;

import com.gabriel.testesistemarecebiveis.controller.dto.PaginaResponse;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orquestra o relatório de Extrato de Liquidação: normaliza e valida o filtro, delega a consulta
 * paginada ao {@link ExtratoLiquidacaoRepository} e enriquece cada linha com o spread derivado do
 * tipo de recebível.
 */
@Service
public class ExtratoLiquidacaoService {

    /** Tamanho de página padrão quando o cliente não informa (ou informa valor inválido). */
    static final int TAMANHO_PAGINA_PADRAO = 20;

    /** Teto de itens por página, para proteger o servidor de páginas excessivamente grandes. */
    static final int TAMANHO_PAGINA_MAXIMO = 200;

    private final ExtratoLiquidacaoRepository repository;
    private final SpreadStrategyResolver spreadStrategyResolver;

    public ExtratoLiquidacaoService(ExtratoLiquidacaoRepository repository,
                                    SpreadStrategyResolver spreadStrategyResolver) {
        this.repository = repository;
        this.spreadStrategyResolver = spreadStrategyResolver;
    }

    /**
     * Gera uma página do relatório de Extrato de Liquidação.
     *
     * @param filtro critérios de filtragem e paginação
     * @return página com as linhas do extrato e os metadados de paginação
     */
    @Transactional(readOnly = true)
    public PaginaResponse<ExtratoLiquidacaoItem> gerar(ExtratoLiquidacaoFiltro filtro) {
        normalizar(filtro);
        validar(filtro);

        long total = repository.contar(filtro);

        List<ExtratoLiquidacaoItem> itens = total == 0
                ? List.of()
                : repository.buscar(filtro).stream()
                        .map(this::preencherSpread)
                        .toList();

        return PaginaResponse.of(itens, filtro.getPagina(), filtro.getTamanhoPagina(), total);
    }

    /** Aplica valores padrão e limites de paginação. */
    private void normalizar(ExtratoLiquidacaoFiltro filtro) {
        if (filtro.getPagina() < 0) {
            filtro.setPagina(0);
        }
        if (filtro.getTamanhoPagina() <= 0) {
            filtro.setTamanhoPagina(TAMANHO_PAGINA_PADRAO);
        }
        if (filtro.getTamanhoPagina() > TAMANHO_PAGINA_MAXIMO) {
            filtro.setTamanhoPagina(TAMANHO_PAGINA_MAXIMO);
        }
    }

    /** Valida a coerência dos ranges informados. */
    private void validar(ExtratoLiquidacaoFiltro filtro) {
        if (filtro.getDataOperacaoInicio() != null && filtro.getDataOperacaoFim() != null
                && filtro.getDataOperacaoInicio().isAfter(filtro.getDataOperacaoFim())) {
            throw new BusinessException(
                    "A data inicial de operação não pode ser posterior à data final de operação.");
        }
        if (filtro.getDataLiquidacaoInicio() != null && filtro.getDataLiquidacaoFim() != null
                && filtro.getDataLiquidacaoInicio().isAfter(filtro.getDataLiquidacaoFim())) {
            throw new BusinessException(
                    "A data inicial de liquidação não pode ser posterior à data final de liquidação.");
        }
    }

    /** Enriquecimento: deriva o spread do tipo de recebível, deixando-o nulo quando desconhecido. */
    private ExtratoLiquidacaoItem preencherSpread(ExtratoLiquidacaoItem item) {
        return spreadStrategyResolver.tentarResolver(item.tipoRecebivel())
                .map(strategy -> item.comSpread(strategy.spread()))
                .orElse(item);
    }
}
