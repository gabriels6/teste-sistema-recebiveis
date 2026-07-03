package com.gabriel.testesistemarecebiveis.ptax;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Payload da importacao de cotacao PTAX via API.
 *
 * @param moeda codigo da moeda estrangeira a importar (ex.: {@code EUR})
 * @param data  data de referencia (ISO {@code yyyy-MM-dd})
 */
public record ImportacaoPtaxRequest(
        String moeda,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate data) {
}
