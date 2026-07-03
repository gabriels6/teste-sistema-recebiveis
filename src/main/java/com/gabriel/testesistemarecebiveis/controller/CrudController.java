package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api")
public interface CrudController<T, ID> {

    @GetMapping
    ResponseEntity<ApiResponse<List<T>>> findAll();

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<T>> findById(@PathVariable ID id);

    @PostMapping
    ResponseEntity<ApiResponse<T>> create(@RequestBody T entity);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @RequestBody T entity);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id);
}
