package com.gabriel.testesistemarecebiveis.ptax;

import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Tenta importar uma cotacao PTAX assim que a aplicacao termina de inicializar.
 *
 * <p>A importacao roda em uma thread separada (daemon) para nao atrasar a
 * inicializacao, e qualquer falha (incluindo a indisponibilidade do BCB apos as
 * retentativas) e apenas registrada em log — nunca derruba a aplicacao,
 * atendendo ao requisito de "cancelar a chamada e registrar nos logs".
 */
@Component
public class PtaxStartupImporter {

    private static final Logger LOG =
            LoggerFactory.getLogger(PtaxStartupImporter.class);

    private final CotacaoPtaxService cotacaoPtaxService;
    private final PtaxProperties properties;

    public PtaxStartupImporter(CotacaoPtaxService cotacaoPtaxService,
                               PtaxProperties properties) {
        this.cotacaoPtaxService = cotacaoPtaxService;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void aoInicializar() {
        if (!properties.isImportOnStartup()) {
            LOG.debug("Importacao PTAX na inicializacao desabilitada "
                    + "(bcb.ptax.import-on-startup=false).");
            return;
        }
        Thread thread = new Thread(this::importar, "ptax-startup-import");
        thread.setDaemon(true);
        thread.start();
    }

    private void importar() {
        String moeda = properties.getMoedaPadrao();
        LocalDate data = ultimoDiaUtil(LocalDate.now());
        LOG.info("Importando cotacao PTAX na inicializacao: {} em {}...",
                moeda, data);
        try {
            TaxaCambio taxa = cotacaoPtaxService.importar(moeda, data);
            LOG.info("Cotacao PTAX importada na inicializacao: {} -> {} = {}",
                    moeda, properties.getMoedaDestino(), taxa.getValor());
        } catch (RuntimeException ex) {
            // Best-effort: registra e segue; a aplicacao continua no ar.
            LOG.error("Nao foi possivel importar a cotacao PTAX na inicializacao "
                    + "({} em {}): {}", moeda, data, ex.getMessage());
        }
    }

    /** Retrocede para o dia util mais recente (o PTAX nao publica em fins de semana). */
    private LocalDate ultimoDiaUtil(LocalDate data) {
        LocalDate d = data;
        while (d.getDayOfWeek() == DayOfWeek.SATURDAY
                || d.getDayOfWeek() == DayOfWeek.SUNDAY) {
            d = d.minusDays(1);
        }
        return d;
    }
}
