package com.gabriel.testesistemarecebiveis.exception;

/**
 * Sinaliza que o servico PTAX do Banco Central esta indisponivel ou nao respondeu
 * apos todas as tentativas. Traduzida para 503 (Service Unavailable), pois nao e
 * um erro do cliente nem uma falha interna desta aplicacao.
 */
public class PtaxIndisponivelException extends RuntimeException {

    public PtaxIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
