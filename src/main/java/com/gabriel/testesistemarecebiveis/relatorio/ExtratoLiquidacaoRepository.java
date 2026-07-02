package com.gabriel.testesistemarecebiveis.relatorio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consulta o Extrato de Liquidação diretamente em SQL nativo, montando dinamicamente apenas as
 * condições de filtro informadas.
 *
 * <p>Optou-se por SQL nativo com WHERE dinâmico (em vez do padrão {@code :param IS NULL OR coluna =
 * :param}) para que o PostgreSQL utilize os índices das colunas filtradas — essencial para responder
 * em segundos sobre milhões de transações. A ordenação {@code data_liquidacao DESC NULLS LAST, id
 * DESC} casa com o índice {@code idx_transacao_data_liquidacao} e garante paginação estável.</p>
 */
@Repository
public class ExtratoLiquidacaoRepository {

    private static final String BASE_JOINS = """
            FROM transacao t
            JOIN recebivel r ON r.id = t.id_recebivel
            JOIN cedente c ON c.id = r.id_cedente
            JOIN moeda mo ON mo.id = t.id_moeda
            """;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca a página de linhas do extrato que satisfazem o filtro. O spread não é preenchido aqui
     * (fica como {@code null}); ele é derivado do tipo de recebível na camada de serviço.
     *
     * @param filtro critérios de filtragem e paginação
     * @return as linhas da página solicitada
     */
    public List<ExtratoLiquidacaoItem> buscar(ExtratoLiquidacaoFiltro filtro) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        String where = montarWhere(filtro, parametros);

        String sql = """
                SELECT
                    t.id AS transacaoId,
                    t.data_operacao AS dataOperacao,
                    t.data_liquidacao AS dataLiquidacao,
                    t.qtde_operacao AS valorFace,
                    mo.cod_moeda AS moedaOperacao,
                    r.cod_ativo AS codAtivo,
                    tr.nome AS tipoRecebivel,
                    r.taxa_base AS taxaBase,
                    mr.cod_moeda AS moedaRecebivel,
                    c.nome AS cedenteNome,
                    c.cod_empresa AS cedenteCodEmpresa
                """
                + BASE_JOINS
                + "JOIN tipo_recebivel tr ON tr.id = r.id_tipo_recebivel\n"
                + "JOIN moeda mr ON mr.id = r.id_moeda\n"
                + where
                + "ORDER BY t.data_liquidacao DESC NULLS LAST, t.id DESC\n"
                + "LIMIT :limit OFFSET :offset";

        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        parametros.forEach(query::setParameter);
        query.setParameter("limit", filtro.getTamanhoPagina());
        query.setParameter("offset", (long) filtro.getPagina() * filtro.getTamanhoPagina());

        @SuppressWarnings("unchecked")
        List<Tuple> linhas = query.getResultList();
        List<ExtratoLiquidacaoItem> itens = new ArrayList<>(linhas.size());
        for (Tuple linha : linhas) {
            itens.add(mapear(linha));
        }
        return itens;
    }

    /**
     * Conta o total de linhas que satisfazem o filtro, para os metadados de paginação.
     *
     * @param filtro critérios de filtragem
     * @return total de linhas
     */
    public long contar(ExtratoLiquidacaoFiltro filtro) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        String where = montarWhere(filtro, parametros);

        String sql = "SELECT COUNT(*) " + BASE_JOINS + where;

        Query query = entityManager.createNativeQuery(sql);
        parametros.forEach(query::setParameter);
        return ((Number) query.getSingleResult()).longValue();
    }

    /**
     * Monta a cláusula WHERE incluindo somente os filtros informados e registra seus parâmetros.
     */
    private String montarWhere(ExtratoLiquidacaoFiltro filtro, Map<String, Object> parametros) {
        List<String> condicoes = new ArrayList<>();

        if (filtro.getDataOperacaoInicio() != null) {
            condicoes.add("t.data_operacao >= :dataOperacaoInicio");
            parametros.put("dataOperacaoInicio", filtro.getDataOperacaoInicio());
        }
        if (filtro.getDataOperacaoFim() != null) {
            condicoes.add("t.data_operacao <= :dataOperacaoFim");
            parametros.put("dataOperacaoFim", filtro.getDataOperacaoFim());
        }
        if (filtro.getDataLiquidacaoInicio() != null) {
            condicoes.add("t.data_liquidacao >= :dataLiquidacaoInicio");
            parametros.put("dataLiquidacaoInicio", filtro.getDataLiquidacaoInicio());
        }
        if (filtro.getDataLiquidacaoFim() != null) {
            condicoes.add("t.data_liquidacao <= :dataLiquidacaoFim");
            parametros.put("dataLiquidacaoFim", filtro.getDataLiquidacaoFim());
        }
        if (StringUtils.hasText(filtro.getNomeCedente())) {
            condicoes.add("c.nome ILIKE :nomeCedente");
            parametros.put("nomeCedente", "%" + filtro.getNomeCedente().trim() + "%");
        }
        if (StringUtils.hasText(filtro.getMoeda())) {
            condicoes.add("mo.cod_moeda = :moeda");
            parametros.put("moeda", filtro.getMoeda().trim());
        }

        if (condicoes.isEmpty()) {
            return "";
        }
        return "WHERE " + String.join(" AND ", condicoes) + "\n";
    }

    // Mapeamento posicional (e não por alias): aliases não-aspados sofrem folding de caixa que varia
    // entre PostgreSQL (minúsculas) e H2, o que quebraria o acesso por nome. A ordem abaixo segue
    // exatamente a lista do SELECT.
    private ExtratoLiquidacaoItem mapear(Tuple linha) {
        return new ExtratoLiquidacaoItem(
                toInteger(linha.get(0)),
                toLocalDate(linha.get(1)),
                toLocalDate(linha.get(2)),
                toBigDecimal(linha.get(3)),
                (String) linha.get(4),
                (String) linha.get(5),
                (String) linha.get(6),
                toBigDecimal(linha.get(7)),
                null,
                (String) linha.get(8),
                (String) linha.get(9),
                (String) linha.get(10));
    }

    private Integer toInteger(Object valor) {
        return valor == null ? null : ((Number) valor).intValue();
    }

    private BigDecimal toBigDecimal(Object valor) {
        if (valor == null) {
            return null;
        }
        return valor instanceof BigDecimal bd ? bd : new BigDecimal(valor.toString());
    }

    private LocalDate toLocalDate(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof LocalDate data) {
            return data;
        }
        if (valor instanceof Date data) {
            return data.toLocalDate();
        }
        return LocalDate.parse(valor.toString());
    }
}
