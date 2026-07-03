package com.gabriel.testesistemarecebiveis.relatorio;

import com.gabriel.testesistemarecebiveis.controller.dto.PaginaResponse;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.ChequePreDatadoStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.DuplicataMercantilStrategy;
import com.gabriel.testesistemarecebiveis.precificacao.strategy.SpreadStrategyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtratoLiquidacaoServiceTest {

    @Mock
    private ExtratoLiquidacaoRepository repository;

    private ExtratoLiquidacaoService service;

    @BeforeEach
    void setUp() {
        SpreadStrategyResolver resolver = new SpreadStrategyResolver(
                List.of(new DuplicataMercantilStrategy(), new ChequePreDatadoStrategy()));
        service = new ExtratoLiquidacaoService(repository, resolver);
    }

    private ExtratoLiquidacaoItem item(String tipoRecebivel) {
        return new ExtratoLiquidacaoItem(1, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10),
                new BigDecimal("1000.00"), "BRL", "ATV1", tipoRecebivel, new BigDecimal("0.01"),
                null, "BRL", "Empresa A", "EMP001");
    }

    @Test
    void deveEnriquecerSpreadComBaseNoTipoRecebivel() {
        when(repository.contar(any())).thenReturn(1L);
        when(repository.buscar(any())).thenReturn(List.of(item("Duplicata Mercantil")));

        PaginaResponse<ExtratoLiquidacaoItem> pagina =
                service.gerar(ExtratoLiquidacaoFiltro.builder().build());

        assertThat(pagina.getConteudo()).hasSize(1);
        assertThat(pagina.getConteudo().get(0).spread()).isEqualByComparingTo("0.015");
        assertThat(pagina.getTotalElementos()).isEqualTo(1L);
        assertThat(pagina.getTotalPaginas()).isEqualTo(1);
    }

    @Test
    void deveDeixarSpreadNuloQuandoTipoSemEstrategia() {
        when(repository.contar(any())).thenReturn(1L);
        when(repository.buscar(any())).thenReturn(List.of(item("Tipo Desconhecido")));

        PaginaResponse<ExtratoLiquidacaoItem> pagina =
                service.gerar(ExtratoLiquidacaoFiltro.builder().build());

        assertThat(pagina.getConteudo().get(0).spread()).isNull();
    }

    @Test
    void naoDeveConsultarLinhasQuandoTotalZero() {
        when(repository.contar(any())).thenReturn(0L);

        PaginaResponse<ExtratoLiquidacaoItem> pagina =
                service.gerar(ExtratoLiquidacaoFiltro.builder().build());

        assertThat(pagina.getConteudo()).isEmpty();
        assertThat(pagina.getTotalPaginas()).isZero();
        verify(repository, never()).buscar(any());
    }

    @Test
    void deveAplicarTamanhoPaginaPadraoQuandoInvalido() {
        when(repository.contar(any())).thenReturn(0L);
        ExtratoLiquidacaoFiltro filtro = ExtratoLiquidacaoFiltro.builder()
                .pagina(-5)
                .tamanhoPagina(0)
                .build();

        PaginaResponse<ExtratoLiquidacaoItem> pagina = service.gerar(filtro);

        assertThat(pagina.getPagina()).isZero();
        assertThat(pagina.getTamanhoPagina()).isEqualTo(ExtratoLiquidacaoService.TAMANHO_PAGINA_PADRAO);
    }

    @Test
    void deveLimitarTamanhoPaginaAoMaximo() {
        when(repository.contar(any())).thenReturn(0L);
        ExtratoLiquidacaoFiltro filtro = ExtratoLiquidacaoFiltro.builder()
                .tamanhoPagina(10_000)
                .build();

        PaginaResponse<ExtratoLiquidacaoItem> pagina = service.gerar(filtro);

        assertThat(pagina.getTamanhoPagina()).isEqualTo(ExtratoLiquidacaoService.TAMANHO_PAGINA_MAXIMO);
    }

    @Test
    void deveRejeitarRangeOperacaoInvertido() {
        ExtratoLiquidacaoFiltro filtro = ExtratoLiquidacaoFiltro.builder()
                .dataOperacaoInicio(LocalDate.of(2026, 3, 1))
                .dataOperacaoFim(LocalDate.of(2026, 1, 1))
                .build();

        assertThatThrownBy(() -> service.gerar(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("operação");
    }

    @Test
    void deveRejeitarRangeLiquidacaoInvertido() {
        ExtratoLiquidacaoFiltro filtro = ExtratoLiquidacaoFiltro.builder()
                .dataLiquidacaoInicio(LocalDate.of(2026, 3, 1))
                .dataLiquidacaoFim(LocalDate.of(2026, 1, 1))
                .build();

        assertThatThrownBy(() -> service.gerar(filtro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("liquidação");
    }
}
