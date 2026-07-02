package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Transacao;
import com.gabriel.testesistemarecebiveis.service.TransacaoService;
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
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transacao>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Transações listadas com sucesso.", transacaoService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transacao>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Transação encontrada com sucesso.", transacaoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transacao>> create(@RequestBody Transacao transacao) {
        return ResponseEntity.status(201).body(ApiResponse.success("Transação criada com sucesso.", transacaoService.create(transacao)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transacao>> update(@PathVariable Integer id, @RequestBody Transacao transacao) {
        return ResponseEntity.ok(ApiResponse.success("Transação atualizada com sucesso.", transacaoService.update(id, transacao)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        transacaoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Transação removida com sucesso.", null));
    }
}
