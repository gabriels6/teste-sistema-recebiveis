package com.gabriel.testesistemarecebiveis.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Indica se a operacao foi bem-sucedida. */
    private boolean success;

    /**
     * Codigo HTTP da resposta (presente apenas em erros), deixando explicito se
     * o problema foi do cliente (4xx) ou do servidor (5xx).
     */
    private Integer status;

    /**
     * Categoria do erro em formato legivel por maquina (ex.: {@code
     * DADOS_INVALIDOS}, {@code NAO_AUTENTICADO}, {@code ERRO_INTERNO}). Presente
     * apenas em erros. Ver {@code exception.ApiErro}.
     */
    private String errorCode;

    /** Mensagem descritiva, voltada ao consumidor da API. */
    private String message;

    /** Carga util em respostas de sucesso. */
    private T data;

    /** Detalhes adicionais do erro (ex.: lista de campos invalidos). */
    private List<String> errors;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Cria uma resposta de erro categorizada.
     *
     * @param status    codigo HTTP correspondente
     * @param errorCode categoria do erro (ver {@code exception.ApiErro})
     * @param message   mensagem descritiva
     * @param errors    detalhes adicionais (opcional)
     */
    public static <T> ApiResponse<T> error(int status, String errorCode,
                                           String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .build();
    }
}
