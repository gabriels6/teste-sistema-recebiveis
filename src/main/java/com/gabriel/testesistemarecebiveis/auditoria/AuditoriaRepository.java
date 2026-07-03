package com.gabriel.testesistemarecebiveis.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acesso a trilha de auditoria.
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    /**
     * Lista os registros de auditoria de uma entidade especifica, do mais
     * recente para o mais antigo.
     *
     * @param entidade nome da entidade auditada
     * @return registros de auditoria da entidade
     */
    List<Auditoria> findByEntidadeOrderByDataHoraDesc(String entidade);

    /**
     * Lista os registros de auditoria de um registro especifico de uma
     * entidade, do mais recente para o mais antigo.
     *
     * @param entidade   nome da entidade auditada
     * @param entidadeId identificador do registro auditado
     * @return registros de auditoria daquele registro
     */
    List<Auditoria> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(
            String entidade, String entidadeId);
}
