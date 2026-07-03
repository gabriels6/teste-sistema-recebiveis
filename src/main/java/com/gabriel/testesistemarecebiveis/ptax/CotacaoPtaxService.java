package com.gabriel.testesistemarecebiveis.ptax;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.PtaxIndisponivelException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.TaxaCambioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Importa cotacoes de fechamento PTAX do Banco Central e as persiste como
 * {@link TaxaCambio} (moeda estrangeira -> Real).
 *
 * <p>A chamada HTTP e feita com ate {@code bcb.ptax.tentativas} tentativas; se
 * todas falharem, a chamada e cancelada e uma {@link PtaxIndisponivelException}
 * (503) e lancada. A gravacao e idempotente: reimportar a mesma moeda/data
 * atualiza o valor em vez de duplicar o registro.
 */
@Service
public class CotacaoPtaxService {

    private static final Logger LOG =
            LoggerFactory.getLogger(CotacaoPtaxService.class);

    private final PtaxClient ptaxClient;
    private final TaxaCambioRepository taxaCambioRepository;
    private final MoedaRepository moedaRepository;
    private final PtaxProperties properties;

    public CotacaoPtaxService(PtaxClient ptaxClient,
                              TaxaCambioRepository taxaCambioRepository,
                              MoedaRepository moedaRepository,
                              PtaxProperties properties) {
        this.ptaxClient = ptaxClient;
        this.taxaCambioRepository = taxaCambioRepository;
        this.moedaRepository = moedaRepository;
        this.properties = properties;
    }

    /**
     * Importa a cotacao de fechamento PTAX de uma moeda em uma data e a grava.
     *
     * @param moeda codigo da moeda estrangeira (ex.: {@code EUR})
     * @param data  data de referencia
     * @return a taxa de cambio criada ou atualizada
     * @throws BusinessException         se os parametros forem invalidos ou nao
     *                                   houver cotacao para a data
     * @throws PtaxIndisponivelException se o servico do BCB nao responder
     */
    @Transactional
    public TaxaCambio importar(String moeda, LocalDate data) {
        if (!StringUtils.hasText(moeda)) {
            throw new BusinessException("A moeda a importar e obrigatoria.");
        }
        if (data == null) {
            throw new BusinessException("A data da cotacao e obrigatoria.");
        }

        String codMoeda = moeda.trim().toUpperCase();
        PtaxCotacaoResponse.Cotacao cotacao = buscarComRetentativas(codMoeda, data)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "Nenhuma cotacao de Fechamento PTAX encontrada para "
                        + codMoeda + " em " + data
                        + " (verifique se e um dia util com cotacao publicada)."));

        return salvar(codMoeda, data, cotacao);
    }

    /** Executa a chamada HTTP com ate {@code tentativas} tentativas. */
    private List<PtaxCotacaoResponse.Cotacao> buscarComRetentativas(
            String moeda, LocalDate data) {
        int maxTentativas = Math.max(1, properties.getTentativas());
        RuntimeException ultimoErro = null;

        for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            try {
                return ptaxClient.buscarCotacoes(moeda, data);
            } catch (RuntimeException ex) {
                ultimoErro = ex;
                LOG.warn("Falha ao consultar PTAX (tentativa {}/{}) para {} em {}: {}",
                        tentativa, maxTentativas, moeda, data, ex.getMessage());
                if (tentativa < maxTentativas) {
                    aguardarBackoff();
                }
            }
        }

        throw new PtaxIndisponivelException(
                "Nao foi possivel obter a cotacao PTAX apos " + maxTentativas
                + " tentativa(s). Chamada cancelada.", ultimoErro);
    }

    private void aguardarBackoff() {
        try {
            Thread.sleep(properties.getBackoffMs());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PtaxIndisponivelException(
                    "Importacao PTAX interrompida.", ex);
        }
    }

    private TaxaCambio salvar(String codMoeda, LocalDate data,
                             PtaxCotacaoResponse.Cotacao cotacao) {
        java.math.BigDecimal valor = cotacao.cotacaoVenda() != null
                ? cotacao.cotacaoVenda()
                : cotacao.cotacaoCompra();
        if (valor == null) {
            throw new BusinessException(
                    "Cotacao PTAX retornada sem valor de venda para " + codMoeda + ".");
        }

        Moeda origem = obterOuCriarMoeda(codMoeda);
        Moeda destino = obterOuCriarMoeda(properties.getMoedaDestino());

        TaxaCambio taxa = taxaCambioRepository
                .findByMoedaOrigem_CodMoedaIgnoreCaseAndMoedaDestino_CodMoedaIgnoreCaseAndDataReferencia(
                        codMoeda, destino.getCodMoeda(), data)
                .orElseGet(TaxaCambio::new);

        taxa.setMoedaOrigem(origem);
        taxa.setMoedaDestino(destino);
        taxa.setDataReferencia(data);
        taxa.setValor(valor);

        TaxaCambio salva = taxaCambioRepository.save(taxa);
        LOG.info("Cotacao PTAX importada: {} -> {} em {} = {}",
                codMoeda, destino.getCodMoeda(), data, valor);
        return salva;
    }

    /** Busca a moeda pelo codigo (case-insensitive) ou a cria, se ausente. */
    private Moeda obterOuCriarMoeda(String codMoeda) {
        return moedaRepository.findByCodMoedaIgnoreCase(codMoeda)
                .orElseGet(() -> moedaRepository.save(
                        Moeda.builder().codMoeda(codMoeda).build()));
    }
}
