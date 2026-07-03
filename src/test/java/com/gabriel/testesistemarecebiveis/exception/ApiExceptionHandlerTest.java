package com.gabriel.testesistemarecebiveis.exception;

import com.gabriel.testesistemarecebiveis.controller.CedenteController;
import com.gabriel.testesistemarecebiveis.service.CedenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que o tratamento global de erros categoriza corretamente cada tipo
 * de falha (dado do cliente 4xx x erro do servidor 5xx) no padrao
 * {@code ApiResponse}. Usa {@code standaloneSetup} com o advice registrado.
 */
@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private CedenteService cedenteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CedenteController(cedenteService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void regraDeNegocioViolada_retorna400DadosInvalidos() throws Exception {
        when(cedenteService.create(any())).thenThrow(
                new BusinessException("O nome do cedente e obrigatorio."));

        mockMvc.perform(post("/api/cedentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codEmpresa\":\"EMP001\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("DADOS_INVALIDOS"))
                .andExpect(jsonPath("$.message")
                        .value("O nome do cedente e obrigatorio."));
    }

    @Test
    void recursoInexistente_retorna404() throws Exception {
        when(cedenteService.findById(eq(99))).thenThrow(
                new ResourceNotFoundException("Cedente", 99));

        mockMvc.perform(get("/api/cedentes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode")
                        .value("RECURSO_NAO_ENCONTRADO"));
    }

    @Test
    void corpoMalformado_retorna400RequisicaoMalformada() throws Exception {
        mockMvc.perform(post("/api/cedentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json valido "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("REQUISICAO_MALFORMADA"));
    }

    @Test
    void parametroDeTipoIncompativel_retorna400() throws Exception {
        // /api/cedentes/{id} espera Integer; "abc" dispara type mismatch.
        mockMvc.perform(get("/api/cedentes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("REQUISICAO_MALFORMADA"));
    }

    @Test
    void falhaInesperada_retorna500ErroInterno() throws Exception {
        when(cedenteService.findAll())
                .thenThrow(new RuntimeException("boom interno"));

        mockMvc.perform(get("/api/cedentes"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.errorCode").value("ERRO_INTERNO"))
                // A mensagem interna nao pode vazar para o cliente.
                .andExpect(jsonPath("$.message")
                        .value("Ocorreu um erro interno. Tente novamente mais tarde."));
    }
}
