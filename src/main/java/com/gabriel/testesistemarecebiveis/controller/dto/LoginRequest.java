package com.gabriel.testesistemarecebiveis.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String nome;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
