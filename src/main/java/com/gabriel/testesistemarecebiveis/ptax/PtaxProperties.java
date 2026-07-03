package com.gabriel.testesistemarecebiveis.ptax;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuracao da importacao de cotacoes PTAX do Banco Central (servico Olinda).
 * Todos os valores podem ser sobrescritos via {@code application.properties}
 * sob o prefixo {@code bcb.ptax}.
 */
@Component
@ConfigurationProperties(prefix = "bcb.ptax")
@Getter
@Setter
public class PtaxProperties {

    /** URL base do recurso OData CotacaoMoedaDia (com placeholders de path). */
    private String url = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1"
            + "/odata/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)";

    /** Numero maximo de tentativas da chamada HTTP (1 inicial + retries). */
    private int tentativas = 3;

    /** Intervalo (ms) de espera entre tentativas. */
    private long backoffMs = 500;

    /** Timeout (ms) para estabelecer conexao. */
    private int connectTimeoutMs = 3000;

    /** Timeout (ms) para leitura da resposta. */
    private int readTimeoutMs = 5000;

    /** Se {@code true}, tenta importar uma cotacao ao inicializar a aplicacao. */
    private boolean importOnStartup = true;

    /** Moeda importada por padrao na inicializacao (ex.: EUR). */
    private String moedaPadrao = "EUR";

    /** Moeda de destino das cotacoes (o PTAX cota sempre contra o Real). */
    private String moedaDestino = "BRL";
}
