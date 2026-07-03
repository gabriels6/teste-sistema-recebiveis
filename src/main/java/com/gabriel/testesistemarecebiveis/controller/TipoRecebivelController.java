package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.service.TipoRecebivelService;
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
@RequestMapping("/api/tipos-recebiveis")
public class TipoRecebivelController {

    private final TipoRecebivelService tipoRecebivelService;

    public TipoRecebivelController(TipoRecebivelService tipoRecebivelService) {
        this.tipoRecebivelService = tipoRecebivelService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoRecebivel>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Tipos de recebíveis listados com sucesso.", tipoRecebivelService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoRecebivel>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Tipo de recebível encontrado com sucesso.", tipoRecebivelService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TipoRecebivel>> create(@RequestBody TipoRecebivel tipoRecebivel) {
        return ResponseEntity.status(201).body(ApiResponse.success("Tipo de recebível criado com sucesso.", tipoRecebivelService.create(tipoRecebivel)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoRecebivel>> update(@PathVariable Integer id, @RequestBody TipoRecebivel tipoRecebivel) {
        return ResponseEntity.ok(ApiResponse.success("Tipo de recebível atualizado com sucesso.", tipoRecebivelService.update(id, tipoRecebivel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        tipoRecebivelService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tipo de recebível removido com sucesso.", null));
    }
}
