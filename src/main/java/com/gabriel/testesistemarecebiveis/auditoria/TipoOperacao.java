package com.gabriel.testesistemarecebiveis.auditoria;

/**
 * Tipo de operacao registrada na trilha de auditoria, correspondendo aos
 * eventos de persistencia disparados para cada entidade.
 */
public enum TipoOperacao {

    /** Entidade criada (INSERT). */
    CRIACAO,

    /** Entidade atualizada (UPDATE). */
    ATUALIZACAO,

    /** Entidade removida (DELETE). */
    REMOCAO
}
