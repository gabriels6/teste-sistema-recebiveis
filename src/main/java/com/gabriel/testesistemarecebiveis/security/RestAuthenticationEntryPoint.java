package com.gabriel.testesistemarecebiveis.security;

import com.gabriel.testesistemarecebiveis.exception.ApiErro;
import com.gabriel.testesistemarecebiveis.exception.RespostaErroHttp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Trata requisicoes sem autenticacao valida (token ausente, expirado ou
 * invalido) a recursos protegidos, respondendo 401 no padrao {@code ApiResponse}
 * em vez da pagina/erro padrao do Spring Security.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        RespostaErroHttp.escrever(response, objectMapper,
                ApiErro.NAO_AUTENTICADO,
                "Autenticacao e necessaria para acessar este recurso.");
    }
}
