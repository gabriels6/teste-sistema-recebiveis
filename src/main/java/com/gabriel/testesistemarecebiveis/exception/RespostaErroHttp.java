package com.gabriel.testesistemarecebiveis.exception;

import com.gabriel.testesistemarecebiveis.controller.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Escreve uma resposta de erro no padrao {@link ApiResponse} diretamente no
 * {@link HttpServletResponse}. Necessario nos pontos da cadeia de filtros de
 * seguranca (autenticacao/autorizacao), onde o {@code @RestControllerAdvice}
 * ainda nao atua, garantindo que tais erros usem o mesmo formato dos demais.
 */
public final class RespostaErroHttp {

    private RespostaErroHttp() {
    }

    public static void escrever(HttpServletResponse response,
                                ObjectMapper objectMapper,
                                ApiErro erro,
                                String mensagem) throws IOException {
        response.setStatus(erro.codigo());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> corpo = ApiResponse.error(
                erro.codigo(), erro.name(), mensagem, null);
        response.getWriter().write(objectMapper.writeValueAsString(corpo));
    }
}
