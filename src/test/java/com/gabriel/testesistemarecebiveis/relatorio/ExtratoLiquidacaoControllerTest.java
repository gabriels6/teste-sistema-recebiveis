package com.gabriel.testesistemarecebiveis.relatorio;

import com.gabriel.testesistemarecebiveis.controller.dto.PaginaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExtratoLiquidacaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExtratoLiquidacaoService extratoLiquidacaoService;

    @Captor
    private ArgumentCaptor<ExtratoLiquidacaoFiltro> filtroCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ExtratoLiquidacaoController(extratoLiquidacaoService))
                .build();
    }

    @Test
    void deveGerarExtratoComPaginacao() throws Exception {
        ExtratoLiquidacaoItem item = new ExtratoLiquidacaoItem(1, LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 2, 10), new BigDecimal("1000.00"), "BRL", "ATV1",
                "Duplicata Mercantil", new BigDecimal("0.01"), new BigDecimal("0.015"), "BRL",
                "Empresa A", "EMP001");
        when(extratoLiquidacaoService.gerar(any()))
                .thenReturn(PaginaResponse.of(List.of(item), 0, 20, 1));

        mockMvc.perform(get("/api/relatorios/extrato-liquidacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElementos").value(1))
                .andExpect(jsonPath("$.data.totalPaginas").value(1))
                .andExpect(jsonPath("$.data.conteudo[0].cedenteNome").value("Empresa A"))
                .andExpect(jsonPath("$.data.conteudo[0].tipoRecebivel").value("Duplicata Mercantil"))
                .andExpect(jsonPath("$.data.conteudo[0].spread").value(0.015));
    }

    @Test
    void deveRepassarFiltrosParaOServico() throws Exception {
        when(extratoLiquidacaoService.gerar(filtroCaptor.capture()))
                .thenReturn(PaginaResponse.of(List.of(), 2, 50, 0));

        mockMvc.perform(get("/api/relatorios/extrato-liquidacao")
                        .param("dataOperacaoInicio", "2026-01-01")
                        .param("dataOperacaoFim", "2026-01-31")
                        .param("dataLiquidacaoInicio", "2026-02-01")
                        .param("dataLiquidacaoFim", "2026-02-28")
                        .param("nomeCedente", "Empresa")
                        .param("moeda", "USD")
                        .param("pagina", "2")
                        .param("tamanhoPagina", "50"))
                .andExpect(status().isOk());

        ExtratoLiquidacaoFiltro filtro = filtroCaptor.getValue();
        assertThat(filtro.getDataOperacaoInicio()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(filtro.getDataOperacaoFim()).isEqualTo(LocalDate.of(2026, 1, 31));
        assertThat(filtro.getDataLiquidacaoInicio()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(filtro.getDataLiquidacaoFim()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(filtro.getNomeCedente()).isEqualTo("Empresa");
        assertThat(filtro.getMoeda()).isEqualTo("USD");
        assertThat(filtro.getPagina()).isEqualTo(2);
        assertThat(filtro.getTamanhoPagina()).isEqualTo(50);
    }
}
