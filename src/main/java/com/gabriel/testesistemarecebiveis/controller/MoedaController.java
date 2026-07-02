package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.service.MoedaService;
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
@RequestMapping("/api/moedas")
public class MoedaController {

    private final MoedaService moedaService;

    public MoedaController(MoedaService moedaService) {
        this.moedaService = moedaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Moeda>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Moedas listadas com sucesso.", moedaService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Moeda>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Moeda encontrada com sucesso.", moedaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Moeda>> create(@RequestBody Moeda moeda) {
        return ResponseEntity.status(201).body(ApiResponse.success("Moeda criada com sucesso.", moedaService.create(moeda)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Moeda>> update(@PathVariable Integer id, @RequestBody Moeda moeda) {
        return ResponseEntity.ok(ApiResponse.success("Moeda atualizada com sucesso.", moedaService.update(id, moeda)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        moedaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Moeda removida com sucesso.", null));
    }
}
