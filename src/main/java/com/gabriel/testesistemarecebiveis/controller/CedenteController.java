package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.service.CedenteService;
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
@RequestMapping("/api/cedentes")
public class CedenteController {

    private final CedenteService cedenteService;

    public CedenteController(CedenteService cedenteService) {
        this.cedenteService = cedenteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cedente>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Cedentes listados com sucesso.", cedenteService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cedente>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Cedente encontrado com sucesso.", cedenteService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cedente>> create(@RequestBody Cedente cedente) {
        return ResponseEntity.status(201).body(ApiResponse.success("Cedente criado com sucesso.", cedenteService.create(cedente)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cedente>> update(@PathVariable Integer id, @RequestBody Cedente cedente) {
        return ResponseEntity.ok(ApiResponse.success("Cedente atualizado com sucesso.", cedenteService.update(id, cedente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        cedenteService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cedente removido com sucesso.", null));
    }
}
