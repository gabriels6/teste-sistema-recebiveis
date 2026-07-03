package com.gabriel.testesistemarecebiveis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.service.FuncaoService;
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
class FuncaoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FuncaoService funcaoService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FuncaoController(funcaoService)).build();
    }

    @Test
    void shouldListFuncoes() throws Exception {
        when(funcaoService.findAll()).thenReturn(List.of(Funcao.builder().id(1).nome("ADMIN").build()));

        mockMvc.perform(get("/api/funcaos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nome").value("ADMIN"));
    }

    @Test
    void shouldCreateFuncao() throws Exception {
        Funcao funcao = Funcao.builder().id(1).nome("ADMIN").build();
        when(funcaoService.create(any(Funcao.class))).thenReturn(funcao);

        mockMvc.perform(post("/api/funcaos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(funcao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
