package com.gabriel.testesistemarecebiveis.relatorio;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.controller.dto.PaginaResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Endpoint do relatório de Extrato de Liquidação.
 *
 * <p>Expõe uma consulta paginada que reúne dados da transação, do cálculo do recebível (taxa base,
 * tipo, spread e moedas), valor de face, dados do cedente e data de liquidação. Todos os filtros são
 * opcionais e combináveis.</p>
 */
@RestController
@RequestMapping("/api/relatorios/extrato-liquidacao")
public class ExtratoLiquidacaoController {

    private final ExtratoLiquidacaoService extratoLiquidacaoService;

    public ExtratoLiquidacaoController(ExtratoLiquidacaoService extratoLiquidacaoService) {
        this.extratoLiquidacaoService = extratoLiquidacaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginaResponse<ExtratoLiquidacaoItem>>> gerar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataOperacaoInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataOperacaoFim,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataLiquidacaoInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataLiquidacaoFim,
            @RequestParam(required = false) String nomeCedente,
            @RequestParam(required = false) String moeda,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanhoPagina) {

        ExtratoLiquidacaoFiltro filtro = ExtratoLiquidacaoFiltro.builder()
                .dataOperacaoInicio(dataOperacaoInicio)
                .dataOperacaoFim(dataOperacaoFim)
                .dataLiquidacaoInicio(dataLiquidacaoInicio)
                .dataLiquidacaoFim(dataLiquidacaoFim)
                .nomeCedente(nomeCedente)
                .moeda(moeda)
                .pagina(pagina)
                .tamanhoPagina(tamanhoPagina)
                .build();

        PaginaResponse<ExtratoLiquidacaoItem> resultado = extratoLiquidacaoService.gerar(filtro);
        return ResponseEntity.ok(
                ApiResponse.success("Extrato de liquidação gerado com sucesso.", resultado));
    }
}
