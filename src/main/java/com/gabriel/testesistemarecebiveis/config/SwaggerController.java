package com.gabriel.testesistemarecebiveis.config;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class SwaggerController {

    @GetMapping(value = "/swagger-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> apiDocs() {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("openapi", "3.0.1");
        doc.put("info", Map.of(
                "title", "Sistema de Recebíveis API",
                "version", "1.0.0",
                "description", "API REST para gestão de recebíveis, usuários, taxas e transações."
        ));
        doc.put("paths", new LinkedHashMap<>());
        return doc;
    }
}
