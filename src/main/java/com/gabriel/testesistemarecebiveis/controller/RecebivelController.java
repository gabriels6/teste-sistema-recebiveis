package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Recebivel;
import com.gabriel.testesistemarecebiveis.service.RecebivelService;
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
@RequestMapping("/api/recebiveis")
public class RecebivelController {

    private final RecebivelService recebivelService;

    public RecebivelController(RecebivelService recebivelService) {
        this.recebivelService = recebivelService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Recebivel>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Recebíveis listados com sucesso.", recebivelService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Recebivel>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Recebível encontrado com sucesso.", recebivelService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Recebivel>> create(@RequestBody Recebivel recebivel) {
        return ResponseEntity.status(201).body(ApiResponse.success("Recebível criado com sucesso.", recebivelService.create(recebivel)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Recebivel>> update(@PathVariable Integer id, @RequestBody Recebivel recebivel) {
        return ResponseEntity.ok(ApiResponse.success("Recebível atualizado com sucesso.", recebivelService.update(id, recebivel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        recebivelService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Recebível removido com sucesso.", null));
    }
}
