package com.gabriel.testesistemarecebiveis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que erros de autenticacao na cadeia de filtros de seguranca (que nao
 * chegam ao {@code @RestControllerAdvice}) tambem respondem no padrao
 * {@code ApiResponse}, com 401 explicito.
 */
@SpringBootTest
@ActiveProfiles("test")
class SecurityErrorIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void semToken_emRecursoProtegido_retorna401NaoAutenticado() throws Exception {
        mockMvc.perform(get("/api/cedentes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.errorCode").value("NAO_AUTENTICADO"));
    }

    @Test
    void tokenInvalido_emRecursoProtegido_retorna401() throws Exception {
        mockMvc.perform(get("/api/cedentes")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("NAO_AUTENTICADO"));
    }
}
