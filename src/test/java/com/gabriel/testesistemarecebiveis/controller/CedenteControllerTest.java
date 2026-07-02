package com.gabriel.testesistemarecebiveis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.service.CedenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CedenteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CedenteService cedenteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CedenteController(cedenteService)).build();
    }

    @Test
    void shouldListCedentes() throws Exception {
        when(cedenteService.findAll()).thenReturn(List.of(Cedente.builder().id(1).codEmpresa("EMP001").nome("Empresa A").build()));

        mockMvc.perform(get("/api/cedentes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nome").value("Empresa A"));
    }

    @Test
    void shouldCreateCedente() throws Exception {
        Cedente cedente = Cedente.builder().id(1).codEmpresa("EMP001").nome("Empresa A").build();
        when(cedenteService.create(any(Cedente.class))).thenReturn(cedente);

        mockMvc.perform(post("/api/cedentes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cedente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
