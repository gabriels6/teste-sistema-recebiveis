package com.gabriel.testesistemarecebiveis.auditoria;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expoe (somente leitura) a trilha de auditoria da aplicacao.
 */
@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /**
     * Lista os registros de auditoria. Sem parametros, retorna todos; com
     * {@code entidade} (e opcionalmente {@code entidadeId}), filtra o resultado.
     *
     * @param entidade   nome da entidade a filtrar (opcional)
     * @param entidadeId identificador do registro auditado (opcional)
     * @return registros de auditoria
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Auditoria>>> listar(
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) String entidadeId) {
        List<Auditoria> registros = (entidade == null || entidade.isBlank())
                ? auditoriaService.findAll()
                : auditoriaService.findByEntidade(entidade, entidadeId);
        return ResponseEntity.ok(ApiResponse.success(
                "Registros de auditoria listados com sucesso.", registros));
    }
}
