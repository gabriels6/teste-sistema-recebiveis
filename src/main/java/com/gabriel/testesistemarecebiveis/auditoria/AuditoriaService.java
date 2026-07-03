package com.gabriel.testesistemarecebiveis.auditoria;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Consulta a trilha de auditoria. A gravacao dos registros e feita de forma
 * transversal por {@link AuditoriaEventListener}; este servico expoe apenas a
 * leitura.
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Auditoria> findAll() {
        return auditoriaRepository.findAll();
    }

    /**
     * Lista registros de auditoria, filtrando por entidade e, opcionalmente,
     * por identificador do registro auditado.
     *
     * @param entidade   nome da entidade (ex.: {@code Cedente})
     * @param entidadeId identificador do registro; se vazio, retorna todos os
     *                   registros da entidade
     * @return registros de auditoria, do mais recente para o mais antigo
     */
    @Transactional(readOnly = true)
    public List<Auditoria> findByEntidade(String entidade, String entidadeId) {
        if (StringUtils.hasText(entidadeId)) {
            return auditoriaRepository
                    .findByEntidadeAndEntidadeIdOrderByDataHoraDesc(
                            entidade, entidadeId);
        }
        return auditoriaRepository.findByEntidadeOrderByDataHoraDesc(entidade);
    }
}
