package com.gabriel.testesistemarecebiveis.exception;

import org.springframework.http.HttpStatus;

/**
 * Categorias de erro da API, cada uma associada a um status HTTP. Serve como
 * fonte unica de verdade para o tratamento global de excecoes, deixando
 * explicito se o erro e do cliente (4xx) ou do servidor (5xx).
 */
public enum ApiErro {

    /** Dado obrigatorio ausente ou regra de negocio violada. */
    DADOS_INVALIDOS(HttpStatus.BAD_REQUEST),

    /** Requisicao malformada: corpo ilegivel, tipo incompativel, parametro ausente. */
    REQUISICAO_MALFORMADA(HttpStatus.BAD_REQUEST),

    /** Credenciais de login invalidas. */
    CREDENCIAIS_INVALIDAS(HttpStatus.UNAUTHORIZED),

    /** Requisicao sem autenticacao valida (token ausente/expirado/invalido). */
    NAO_AUTENTICADO(HttpStatus.UNAUTHORIZED),

    /** Usuario bloqueado por excesso de tentativas. */
    USUARIO_BLOQUEADO(HttpStatus.LOCKED),

    /** Usuario autenticado, porem sem permissao para o recurso. */
    ACESSO_NEGADO(HttpStatus.FORBIDDEN),

    /** Recurso solicitado nao existe. */
    RECURSO_NAO_ENCONTRADO(HttpStatus.NOT_FOUND),

    /** Metodo HTTP nao suportado pelo recurso. */
    METODO_NAO_PERMITIDO(HttpStatus.METHOD_NOT_ALLOWED),

    /** Conflito de estado (ex.: concorrencia ou violacao de integridade). */
    CONFLITO(HttpStatus.CONFLICT),

    /** Dependencia externa indisponivel (ex.: servico PTAX do Banco Central). */
    SERVICO_INDISPONIVEL(HttpStatus.SERVICE_UNAVAILABLE),

    /** Falha inesperada do servidor. */
    ERRO_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ApiErro(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }

    public int codigo() {
        return status.value();
    }
}
