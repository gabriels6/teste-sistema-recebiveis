package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import com.gabriel.testesistemarecebiveis.ptax.CotacaoPtaxService;
import com.gabriel.testesistemarecebiveis.ptax.ImportacaoPtaxRequest;
import com.gabriel.testesistemarecebiveis.service.TaxaCambioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/taxas-cambio")
public class TaxaCambioController {

    private final TaxaCambioService taxaCambioService;
    private final CotacaoPtaxService cotacaoPtaxService;

    public TaxaCambioController(TaxaCambioService taxaCambioService,
                               CotacaoPtaxService cotacaoPtaxService) {
        this.taxaCambioService = taxaCambioService;
        this.cotacaoPtaxService = cotacaoPtaxService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxaCambio>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Taxas de câmbio listadas com sucesso.", taxaCambioService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxaCambio>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Taxa de câmbio encontrada com sucesso.", taxaCambioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaxaCambio>> create(@RequestBody TaxaCambio taxaCambio) {
        return ResponseEntity.status(201).body(ApiResponse.success("Taxa de câmbio criada com sucesso.", taxaCambioService.create(taxaCambio)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaxaCambio>> update(@PathVariable Integer id, @RequestBody TaxaCambio taxaCambio) {
        return ResponseEntity.ok(ApiResponse.success("Taxa de câmbio atualizada com sucesso.", taxaCambioService.update(id, taxaCambio)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        taxaCambioService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Taxa de câmbio removida com sucesso.", null));
    }

    /**
     * Importa a cotação de fechamento PTAX (Banco Central) de uma moeda em uma
     * data e a grava como taxa de câmbio (moeda → Real). Idempotente.
     */
    @PostMapping("/importacoes-ptax")
    public ResponseEntity<ApiResponse<TaxaCambio>> importarPtax(
            @RequestBody ImportacaoPtaxRequest request) {
        TaxaCambio taxa = cotacaoPtaxService.importar(request.moeda(), request.data());
        return ResponseEntity.status(201).body(ApiResponse.success(
                "Cotação PTAX importada com sucesso.", taxa));
    }
}
