package com.gabriel.testesistemarecebiveis.controller;

import com.gabriel.testesistemarecebiveis.controller.dto.AuthResponse;
import com.gabriel.testesistemarecebiveis.controller.dto.LoginRequest;
import com.gabriel.testesistemarecebiveis.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void shouldReturnTokenWhenLoginIsSuccessful() throws Exception {
        AuthResponse authResponse = AuthResponse.builder()
                .token("token123")
                .message("Login efetuado com sucesso.")
                .remainingAttempts(3)
                .build();

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(authResponse);

        LoginRequest request = new LoginRequest("usuario", "senha123");
        String json = new ObjectMapper().writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Login efetuado com sucesso."))
                .andExpect(jsonPath("$.remainingAttempts").value(3));
    }
}
