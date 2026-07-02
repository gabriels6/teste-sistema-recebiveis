package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Usuários listados com sucesso.", usuarioService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Usuário encontrado com sucesso.", usuarioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Usuario>> create(@RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(ApiResponse.success("Usuário criado com sucesso.", usuarioService.create(usuario)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> update(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.success("Usuário atualizado com sucesso.", usuarioService.update(id, usuario)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Usuário removido com sucesso.", null));
    }
}
