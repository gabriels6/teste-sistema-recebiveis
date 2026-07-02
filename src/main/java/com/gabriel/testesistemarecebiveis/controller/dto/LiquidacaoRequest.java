package com.gabriel.testesistemarecebiveis.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacaoRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataLiquidacao;
}
