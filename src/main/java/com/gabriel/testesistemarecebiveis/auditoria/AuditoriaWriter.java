package com.gabriel.testesistemarecebiveis.auditoria;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

/**
 * Persiste os registros de auditoria diretamente via JDBC.
 *
 * <p>O uso de {@link JdbcTemplate} — em vez do repositorio JPA — e proposital:
 * a gravacao ocorre a partir dos <em>event listeners</em> do Hibernate, durante
 * o ciclo de flush da sessao. Persistir uma entidade JPA nesse momento
 * reentraria no mesmo ciclo (disparando novos eventos e possivel recursao). O
 * INSERT via JDBC compartilha a mesma conexao/transacao gerenciada pelo Spring,
 * garantindo que a auditoria seja atomica em relacao a operacao auditada, sem
 * tocar o contexto de persistencia.
 */
@Component
public class AuditoriaWriter {

    private static final String INSERT_SQL =
            "INSERT INTO auditoria "
            + "(entidade, entidade_id, operacao, usuario, dados, data_hora) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public AuditoriaWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Grava, em lote, os registros de auditoria acumulados.
     *
     * @param registros registros a persistir; ignorado se vazio
     */
    public void persistir(List<Auditoria> registros) {
        if (registros == null || registros.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(INSERT_SQL, registros, registros.size(),
                (ps, aud) -> {
                    ps.setString(1, aud.getEntidade());
                    ps.setString(2, aud.getEntidadeId());
                    ps.setString(3, aud.getOperacao().name());
                    ps.setString(4, aud.getUsuario());
                    if (aud.getDados() == null) {
                        ps.setNull(5, Types.VARCHAR);
                    } else {
                        ps.setString(5, aud.getDados());
                    }
                    ps.setTimestamp(6, Timestamp.valueOf(aud.getDataHora()));
                });
    }
}
