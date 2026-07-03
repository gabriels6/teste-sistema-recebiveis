package com.gabriel.testesistemarecebiveis.auditoria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.repositories.CedenteRepository;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica, de ponta a ponta, que toda operacao de persistencia (criacao,
 * atualizacao e remocao) gera o respectivo registro na trilha de auditoria.
 *
 * <p>O teste nao e transacional: cada {@code save}/{@code delete} de repositorio
 * roda em sua propria transacao e comita, disparando a gravacao da auditoria em
 * {@code beforeCommit} — reproduzindo o comportamento real da aplicacao.
 */
@SpringBootTest
@ActiveProfiles("test")
class AuditoriaIntegrationTest {

    @Autowired
    private CedenteRepository cedenteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncaoRepository funcaoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void limpar() {
        usuarioRepository.deleteAll();
        funcaoRepository.deleteAll();
        cedenteRepository.deleteAll();
        auditoriaRepository.deleteAll();
    }

    @Test
    void registraCriacao() {
        Cedente cedente = cedenteRepository.save(
                Cedente.builder().codEmpresa("EMP001").nome("Alpha").build());

        List<Auditoria> registros = auditoriaRepository
                .findByEntidadeOrderByDataHoraDesc("Cedente");

        assertThat(registros).hasSize(1);
        Auditoria registro = registros.get(0);
        assertThat(registro.getOperacao()).isEqualTo(TipoOperacao.CRIACAO);
        assertThat(registro.getEntidadeId())
                .isEqualTo(cedente.getId().toString());
        assertThat(registro.getUsuario()).isEqualTo("sistema");
        assertThat(registro.getDataHora()).isNotNull();
        assertThat(atual(registro)).containsEntry("nome", "Alpha")
                .containsEntry("codEmpresa", "EMP001");
    }

    @Test
    void registraAtualizacaoComEstadoAnteriorEAtual() {
        Cedente cedente = cedenteRepository.save(
                Cedente.builder().codEmpresa("EMP001").nome("Alpha").build());
        cedente.setNome("Beta");
        cedenteRepository.save(cedente);

        Auditoria atualizacao = auditoriaRepository
                .findByEntidadeAndEntidadeIdOrderByDataHoraDesc(
                        "Cedente", cedente.getId().toString())
                .stream()
                .filter(a -> a.getOperacao() == TipoOperacao.ATUALIZACAO)
                .findFirst()
                .orElseThrow();

        assertThat(anterior(atualizacao)).containsEntry("nome", "Alpha");
        assertThat(atual(atualizacao)).containsEntry("nome", "Beta");
    }

    @Test
    void registraRemocao() {
        Cedente cedente = cedenteRepository.save(
                Cedente.builder().codEmpresa("EMP001").nome("Alpha").build());
        String id = cedente.getId().toString();
        cedenteRepository.delete(cedente);

        Auditoria remocao = auditoriaRepository
                .findByEntidadeAndEntidadeIdOrderByDataHoraDesc("Cedente", id)
                .stream()
                .filter(a -> a.getOperacao() == TipoOperacao.REMOCAO)
                .findFirst()
                .orElseThrow();

        assertThat(anterior(remocao)).containsEntry("nome", "Alpha");
    }

    @Test
    void mascaraCamposSensiveis() {
        Funcao funcao = funcaoRepository.save(
                Funcao.builder().nome("Operador").build());
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .funcao(funcao).nome("joao").hashSenha("segredo-super-secreto")
                .tentativas(0).build());

        Auditoria registro = auditoriaRepository
                .findByEntidadeAndEntidadeIdOrderByDataHoraDesc(
                        "Usuario", usuario.getId().toString())
                .get(0);

        Map<String, Object> atual = atual(registro);
        assertThat(atual).containsEntry("hashSenha", "***");
        assertThat(registro.getDados()).doesNotContain("segredo-super-secreto");
        // A associacao e auditada apenas pelo id, sem inicializar o proxy.
        assertThat(atual).containsEntry("funcao", funcao.getId());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> atual(Auditoria auditoria) {
        return (Map<String, Object>) dados(auditoria).get("atual");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> anterior(Auditoria auditoria) {
        return (Map<String, Object>) dados(auditoria).get("anterior");
    }

    private Map<String, Object> dados(Auditoria auditoria) {
        try {
            return objectMapper.readValue(auditoria.getDados(),
                    new TypeReference<Map<String, Object>>() { });
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Falha ao ler dados de auditoria", ex);
        }
    }
}
