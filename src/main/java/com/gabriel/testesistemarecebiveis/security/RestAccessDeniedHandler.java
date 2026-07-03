package com.gabriel.testesistemarecebiveis.security;

import com.gabriel.testesistemarecebiveis.exception.ApiErro;
import com.gabriel.testesistemarecebiveis.exception.RespostaErroHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Trata requisicoes de usuarios autenticados, porem sem permissao para o
 * recurso, respondendo 403 no padrao {@code ApiResponse}.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        RespostaErroHttp.escrever(response, objectMapper,
                ApiErro.ACESSO_NEGADO,
                "Voce nao tem permissao para acessar este recurso.");
    }
}
