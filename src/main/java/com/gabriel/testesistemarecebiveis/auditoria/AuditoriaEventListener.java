package com.gabriel.testesistemarecebiveis.auditoria;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Listener global de eventos do Hibernate que registra, na trilha de
 * auditoria, toda entidade criada, atualizada ou removida.
 *
 * <p>Por atuar no nivel dos eventos de persistencia (e nao em um servico
 * especifico), cobre automaticamente todas as entidades da aplicacao —
 * inclusive alteracoes em cascata — sem exigir alteracao em cada servico.
 *
 * <p>Cada registro e gravado (via {@link AuditoriaWriter}, com JDBC) no momento
 * em que o evento e disparado, durante o flush da sessao. Como a gravacao usa a
 * mesma conexao/transacao da operacao auditada, a auditoria e atomica: se a
 * transacao de negocio for revertida, o registro de auditoria tambem e. Os
 * eventos {@code POST_*} (nao {@code POST_COMMIT}) sao usados justamente para
 * que a escrita ocorra dentro da transacao, apos o SQL da propria entidade.
 */
@Component
public class AuditoriaEventListener
        implements PostInsertEventListener,
        PostUpdateEventListener,
        PostDeleteEventListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(AuditoriaEventListener.class);

    /** Usuario atribuido quando nao ha contexto de seguranca (ex.: seeds). */
    private static final String USUARIO_SISTEMA = "sistema";

    /** Fragmentos de nomes de campos cujo valor deve ser mascarado. */
    private static final List<String> CAMPOS_SENSIVEIS =
            List.of("senha", "hash", "password", "secret", "token");

    private static final String VALOR_MASCARADO = "***";

    private final AuditoriaWriter auditoriaWriter;

    /**
     * Mapper dedicado a serializacao interna da auditoria. Nao injetamos o bean
     * do contexto porque, no Spring Boot 4, o {@code ObjectMapper} gerenciado e
     * o do Jackson 3 ({@code tools.jackson}); aqui usamos uma instancia propria
     * do Jackson 2, e os valores do snapshot sao reduzidos a tipos escalares
     * (ver {@link #valorSeguro(Object)}), dispensando modulos extras.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditoriaEventListener(AuditoriaWriter auditoriaWriter) {
        this.auditoriaWriter = auditoriaWriter;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (deveIgnorar(event.getEntity())) {
            return;
        }
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("atual", estadoParaMapa(
                event.getState(), event.getPersister(), event.getSession()));
        registrar(event.getPersister(), event.getId(),
                TipoOperacao.CRIACAO, dados);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (deveIgnorar(event.getEntity())) {
            return;
        }
        Map<String, Object> dados = new LinkedHashMap<>();
        if (event.getOldState() != null) {
            dados.put("anterior", estadoParaMapa(
                    event.getOldState(), event.getPersister(),
                    event.getSession()));
        }
        dados.put("atual", estadoParaMapa(
                event.getState(), event.getPersister(), event.getSession()));
        registrar(event.getPersister(), event.getId(),
                TipoOperacao.ATUALIZACAO, dados);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (deveIgnorar(event.getEntity())) {
            return;
        }
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("anterior", estadoParaMapa(
                event.getDeletedState(), event.getPersister(),
                event.getSession()));
        registrar(event.getPersister(), event.getId(),
                TipoOperacao.REMOCAO, dados);
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    /**
     * Constroi e grava, imediatamente, o registro de auditoria da operacao.
     */
    private void registrar(EntityPersister persister, Object id,
                           TipoOperacao operacao, Map<String, Object> dados) {
        Auditoria auditoria = Auditoria.builder()
                .entidade(nomeSimples(persister.getEntityName()))
                .entidadeId(id == null ? null : id.toString())
                .operacao(operacao)
                .usuario(usuarioAtual())
                .dados(serializar(dados))
                .dataHora(LocalDateTime.now())
                .build();

        auditoriaWriter.persistir(List.of(auditoria));
    }

    /**
     * Converte o vetor de estado de uma entidade (na ordem das propriedades do
     * persister) em um mapa nome -> valor seguro para serializacao, extraindo
     * apenas o identificador de associacoes (sem inicializar proxies) e
     * ignorando colecoes.
     */
    private Map<String, Object> estadoParaMapa(
            Object[] estado, EntityPersister persister,
            SharedSessionContractImplementor session) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        if (estado == null) {
            return mapa;
        }
        String[] nomes = persister.getPropertyNames();
        Type[] tipos = persister.getPropertyTypes();
        for (int i = 0; i < nomes.length; i++) {
            String nome = nomes[i];
            Object valor = estado[i];
            if (ehSensivel(nome)) {
                mapa.put(nome, valor == null ? null : VALOR_MASCARADO);
            } else if (valor == null) {
                mapa.put(nome, null);
            } else if (tipos[i].isCollectionType()) {
                // Colecoes nao sao incluidas para evitar inicializacao lazy.
                continue;
            } else if (tipos[i].isEntityType()) {
                // Guarda apenas o id da associacao, sem inicializar o proxy.
                mapa.put(nome, valorSeguro(
                        idAssociacao(tipos[i], valor, session)));
            } else {
                mapa.put(nome, valorSeguro(valor));
            }
        }
        return mapa;
    }

    /**
     * Extrai o identificador de uma associacao sem inicializar o proxy. Para
     * entidades ja gerenciadas basta o contexto de persistencia; para instancias
     * destacadas (ex.: uma associacao carregada em outra transacao) recorremos ao
     * {@link EntityType}, que le o proprio campo identificador.
     */
    private Object idAssociacao(Type tipo, Object valor,
                                SharedSessionContractImplementor session) {
        Object id = session.getContextEntityIdentifier(valor);
        if (id == null && tipo instanceof EntityType entityType) {
            EntityPersister associada = session.getFactory()
                    .getMappingMetamodel()
                    .getEntityDescriptor(entityType.getAssociatedEntityName());
            id = associada.getIdentifier(valor, session);
        }
        return id;
    }

    private String serializar(Object dados) {
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (JsonProcessingException ex) {
            LOG.warn("Falha ao serializar dados de auditoria", ex);
            return null;
        }
    }

    private boolean deveIgnorar(Object entity) {
        // Nunca audita a propria trilha de auditoria.
        return entity instanceof Auditoria;
    }

    /**
     * Reduz um valor a um tipo escalar seguro para serializacao JSON.
     * Numeros, booleanos e textos sao mantidos; os demais (datas, componentes,
     * enums, etc.) sao convertidos para {@code String}, evitando a dependencia
     * de modulos Jackson adicionais e a inicializacao de estruturas complexas.
     */
    private Object valorSeguro(Object valor) {
        if (valor == null
                || valor instanceof Number
                || valor instanceof Boolean
                || valor instanceof String) {
            return valor;
        }
        return valor.toString();
    }

    private boolean ehSensivel(String nome) {
        String alvo = nome.toLowerCase(Locale.ROOT);
        return CAMPOS_SENSIVEIS.stream().anyMatch(alvo::contains);
    }

    private String usuarioAtual() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getName() != null
                && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return USUARIO_SISTEMA;
    }

    private String nomeSimples(String entityName) {
        if (entityName == null) {
            return null;
        }
        int ponto = entityName.lastIndexOf('.');
        return ponto >= 0 ? entityName.substring(ponto + 1) : entityName;
    }
}
