package com.gabriel.testesistemarecebiveis.ptax;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Cliente HTTP do servico OData de cotacoes PTAX do Banco Central. Encapsula a
 * montagem da URI (formato de data norte-americano, filtro por Fechamento PTAX)
 * e os timeouts de conexao/leitura. Nao trata retentativas — isso e
 * responsabilidade de {@link CotacaoPtaxService}.
 */
@Component
public class PtaxClient {

    /** O parametro dataCotacao do PTAX usa o formato norte-americano MM-dd-yyyy. */
    private static final DateTimeFormatter FORMATO_DATA_BCB =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final PtaxProperties properties;
    private final RestClient restClient;

    public PtaxClient(PtaxProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .requestFactory(criarRequestFactory(properties))
                .build();
    }

    private static SimpleClientHttpRequestFactory criarRequestFactory(
            PtaxProperties properties) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeoutMs());
        factory.setReadTimeout(properties.getReadTimeoutMs());
        return factory;
    }

    /**
     * Busca as cotacoes de fechamento PTAX de uma moeda em uma data.
     *
     * @param moeda codigo da moeda estrangeira (ex.: {@code EUR})
     * @param data  data de referencia da cotacao
     * @return lista de cotacoes retornadas (normalmente 0 ou 1)
     */
    public List<PtaxCotacaoResponse.Cotacao> buscarCotacoes(
            String moeda, LocalDate data) {
        URI uri = montarUri(moeda, data);
        PtaxCotacaoResponse resposta = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PtaxCotacaoResponse.class);
        return resposta == null || resposta.value() == null
                ? List.of()
                : resposta.value();
    }

    private URI montarUri(String moeda, LocalDate data) {
        String query = "@moeda='" + moeda + "'"
                + "&@dataCotacao='" + data.format(FORMATO_DATA_BCB) + "'"
                + "&$top=100"
                + "&$filter=tipoBoletim eq 'Fechamento PTAX'"
                + "&$format=json";
        return UriComponentsBuilder.fromUriString(properties.getUrl())
                .query(query)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
    }
}
