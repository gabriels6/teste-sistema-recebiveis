package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.service.FuncaoService;
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
@RequestMapping("/api/funcaos")
public class FuncaoController {

    private final FuncaoService funcaoService;

    public FuncaoController(FuncaoService funcaoService) {
        this.funcaoService = funcaoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Funcao>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Funções listadas com sucesso.", funcaoService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Funcao>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Função encontrada com sucesso.", funcaoService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Funcao>> create(@RequestBody Funcao funcao) {
        return ResponseEntity.status(201).body(ApiResponse.success("Função criada com sucesso.", funcaoService.create(funcao)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Funcao>> update(@PathVariable Integer id, @RequestBody Funcao funcao) {
        return ResponseEntity.ok(ApiResponse.success("Função atualizada com sucesso.", funcaoService.update(id, funcao)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        funcaoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Função removida com sucesso.", null));
    }
}
