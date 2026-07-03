package com.gabriel.testesistemarecebiveis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.testesistemarecebiveis.controller.dto.AuthResponse;
import com.gabriel.testesistemarecebiveis.controller.dto.LoginRequest;
import com.gabriel.testesistemarecebiveis.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
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

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Login efetuado com sucesso."))
                .andExpect(jsonPath("$.remainingAttempts").value(3));
    }
}
