package com.gabriel.testesistemarecebiveis.relatorio;

import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.Recebivel;
import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.entities.Transacao;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(ExtratoLiquidacaoRepository.class)
class ExtratoLiquidacaoRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ExtratoLiquidacaoRepository repository;

    private Moeda brl;
    private Moeda usd;
    private Cedente empresaA;
    private Cedente empresaB;
    private Usuario usuario;
    private TipoRecebivel duplicata;

    @BeforeEach
    void seed() {
        Funcao funcao = em.persist(Funcao.builder().nome("Operador").build());
        usuario = em.persist(Usuario.builder()
                .funcao(funcao).nome("User").hashSenha("h").tentativas(0).build());
        brl = em.persist(Moeda.builder().codMoeda("BRL").build());
        usd = em.persist(Moeda.builder().codMoeda("USD").build());
        empresaA = em.persist(Cedente.builder().codEmpresa("EMP001").nome("Alpha Comercio").build());
        empresaB = em.persist(Cedente.builder().codEmpresa("EMP002").nome("Beta Servicos").build());
        duplicata = em.persist(TipoRecebivel.builder().nome("Duplicata Mercantil").build());

        // qtde_operacao é NUMERIC(10,8): parte inteira de no máximo 2 dígitos.
        // t1: operação 10/01, liquidação 10/02, cedente Alpha, moeda operação BRL
        criarTransacao(empresaA, brl, brl, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 2, 10),
                "11.00");
        // t2: operação 20/01, liquidação 20/02, cedente Beta, moeda operação USD
        criarTransacao(empresaB, usd, brl, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 20),
                "22.00");
        // t3: operação 05/02, ainda não liquidada, cedente Alpha, moeda operação BRL
        criarTransacao(empresaA, brl, brl, LocalDate.of(2026, 2, 5), LocalDate.of(2026, 4, 30), "33.00");

        em.flush();
        em.clear();
    }

    private void criarTransacao(Cedente cedente, Moeda moedaOperacao, Moeda moedaRecebivel,
                                LocalDate dataOperacao, LocalDate dataLiquidacao, String valor) {
        Recebivel recebivel = em.persist(Recebivel.builder()
                .moeda(moedaRecebivel).cedente(cedente).tipoRecebivel(duplicata)
                .codAtivo("ATV").dataVencimento(LocalDate.of(2026, 6, 1))
                .taxaBase(new BigDecimal("0.01000000")).build());
        em.persist(Transacao.builder()
                .dataLiquidacao(dataLiquidacao)
                .usuario(usuario).recebivel(recebivel).moeda(moedaOperacao)
                .dataOperacao(dataOperacao).dataLiquidacao(dataLiquidacao)
                .qtdeOperacao(new BigDecimal(valor)).precoUnitario(new BigDecimal("1.00000000")).build());
    }

    private ExtratoLiquidacaoFiltro.ExtratoLiquidacaoFiltroBuilder filtroBase() {
        return ExtratoLiquidacaoFiltro.builder().pagina(0).tamanhoPagina(20);
    }

    @Test
    void semFiltroRetornaTodasOrdenadasPorLiquidacaoDescNullsLast() {
        List<ExtratoLiquidacaoItem> itens = repository.buscar(filtroBase().build());

        assertThat(repository.contar(filtroBase().build())).isEqualTo(3L);
        // NULLS LAST na ordenação DESC: a não liquidada (t3) vem por último.
        assertThat(itens).extracting(ExtratoLiquidacaoItem::valorFace)
                .extracting(BigDecimal::intValue)
                .containsExactly(33, 22, 11);
    }

    @Test
    void mapeiaTodosOsCamposDaLinha() {
        ExtratoLiquidacaoItem item = repository.buscar(
                filtroBase().nomeCedente("Beta").build()).get(0);

        assertThat(item.transacaoId()).isNotNull();
        assertThat(item.dataOperacao()).isEqualTo(LocalDate.of(2026, 1, 20));
        assertThat(item.dataLiquidacao()).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(item.valorFace()).isEqualByComparingTo("22.00");
        assertThat(item.moedaOperacao()).isEqualTo("USD");
        assertThat(item.moedaRecebivel()).isEqualTo("BRL");
        assertThat(item.tipoRecebivel()).isEqualTo("Duplicata Mercantil");
        assertThat(item.taxaBase()).isEqualByComparingTo("0.01");
        assertThat(item.cedenteNome()).isEqualTo("Beta Servicos");
        assertThat(item.cedenteCodEmpresa()).isEqualTo("EMP002");
    }

    @Test
    void filtraPorRangeDeDataDeOperacao() {
        ExtratoLiquidacaoFiltro filtro = filtroBase()
                .dataOperacaoInicio(LocalDate.of(2026, 1, 15))
                .dataOperacaoFim(LocalDate.of(2026, 2, 28))
                .build();

        assertThat(repository.contar(filtro)).isEqualTo(2L);
        assertThat(repository.buscar(filtro)).extracting(i -> i.valorFace().intValue())
                .containsExactlyInAnyOrder(22, 33);
    }

    @Test
    void filtraPorRangeDeDataDeLiquidacao() {
        ExtratoLiquidacaoFiltro filtro = filtroBase()
                .dataLiquidacaoInicio(LocalDate.of(2026, 2, 1))
                .dataLiquidacaoFim(LocalDate.of(2026, 2, 15))
                .build();

        // Só t1 liquida nesse range; t3 (não liquidada) é excluída.
        assertThat(repository.contar(filtro)).isEqualTo(1L);
        assertThat(repository.buscar(filtro).get(0).valorFace()).isEqualByComparingTo("11.00");
    }

    @Test
    void filtraPorNomeCedenteParcialSemDistincaoDeCaixa() {
        ExtratoLiquidacaoFiltro filtro = filtroBase().nomeCedente("alpha").build();

        assertThat(repository.contar(filtro)).isEqualTo(2L);
        assertThat(repository.buscar(filtro)).allSatisfy(
                i -> assertThat(i.cedenteNome()).isEqualTo("Alpha Comercio"));
    }

    @Test
    void filtraPorMoedaDaOperacao() {
        ExtratoLiquidacaoFiltro filtro = filtroBase().moeda("USD").build();

        assertThat(repository.contar(filtro)).isEqualTo(1L);
        assertThat(repository.buscar(filtro).get(0).moedaOperacao()).isEqualTo("USD");
    }

    @Test
    void combinaFiltrosDeOperacaoLiquidacaoCedenteEMoeda() {
        ExtratoLiquidacaoFiltro filtro = filtroBase()
                .dataOperacaoInicio(LocalDate.of(2026, 1, 1))
                .dataLiquidacaoFim(LocalDate.of(2026, 2, 28))
                .nomeCedente("Alpha")
                .moeda("BRL")
                .build();

        assertThat(repository.contar(filtro)).isEqualTo(1L);
        assertThat(repository.buscar(filtro).get(0).valorFace()).isEqualByComparingTo("11.00");
    }

    @Test
    void paginacaoLimitaResultadosPorPagina() {
        List<ExtratoLiquidacaoItem> pagina0 = repository.buscar(
                filtroBase().tamanhoPagina(2).pagina(0).build());
        List<ExtratoLiquidacaoItem> pagina1 = repository.buscar(
                filtroBase().tamanhoPagina(2).pagina(1).build());

        assertThat(pagina0).hasSize(2);
        assertThat(pagina1).hasSize(1);
        // Sem sobreposição entre páginas (ordenação estável por id como desempate).
        assertThat(pagina1.get(0).transacaoId())
                .isNotIn(pagina0.get(0).transacaoId(), pagina0.get(1).transacaoId());
    }
}
