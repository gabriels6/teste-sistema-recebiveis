package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/precificacao")
public class PrecificacaoController {

    private final MotorPrecificacao motorPrecificacao;

    public PrecificacaoController(MotorPrecificacao motorPrecificacao) {
        this.motorPrecificacao = motorPrecificacao;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResultadoPrecificacao>> precificar(
            @RequestBody PrecificacaoRequest request) {
        if (request == null) {
            throw new BusinessException("Os dados para precificação são obrigatórios.");
        }
        if (request.getPrazoMeses() == null) {
            throw new BusinessException("O prazo em meses é obrigatório.");
        }
        ResultadoPrecificacao resultado = motorPrecificacao.precificar(
                request.getTipoRecebivel(),
                request.getValorFace(),
                request.getTaxaBase(),
                request.getPrazoMeses(),
                request.getMoedaRecebivel(),
                request.getMoeda(),
                request.getDataCambio());
        return ResponseEntity.ok(
                ApiResponse.success("Recebível precificado com sucesso.", resultado));
    }
}
