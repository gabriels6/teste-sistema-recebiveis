package com.gabriel.testesistemarecebiveis.precificacao;

import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.repositories.TaxaCambioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resolve o fator de conversão de câmbio aplicado ao final da precificação.
 *
 * <p>O objetivo é permitir que o {@link MotorPrecificacao} trabalhe sempre com um único fluxo de
 * cálculo: o valor presente é multiplicado pelo fator retornado por este conversor. Quando não há
 * conversão a fazer — moeda alvo ausente ou igual à moeda do recebível — o fator é
 * {@link BigDecimal#ONE}, de modo que a multiplicação não altera o resultado.</p>
 */
@Service
public class ConversorMoeda {

    private final TaxaCambioRepository taxaCambioRepository;

    public ConversorMoeda(TaxaCambioRepository taxaCambioRepository) {
        this.taxaCambioRepository = taxaCambioRepository;
    }

    /**
     * Fator de conversão da moeda do recebível para a moeda alvo na data informada.
     *
     * @param moedaRecebivel código da moeda do recebível (ex.: "BRL")
     * @param moedaAlvo      código da moeda alvo informada como parâmetro (ex.: "USD"); quando nula,
     *                       em branco ou igual à moeda do recebível, não há conversão
     * @param data           data de referência para a busca da taxa de câmbio
     * @return o valor da taxa de câmbio quando há conversão, ou {@link BigDecimal#ONE} caso contrário
     * @throws BusinessException se houver conversão a fazer mas faltarem dados obrigatórios ou não
     *                           existir taxa de câmbio cadastrada
     */
    public BigDecimal fatorConversao(String moedaRecebivel, String moedaAlvo, LocalDate data) {
        if (semConversao(moedaRecebivel, moedaAlvo)) {
            return BigDecimal.ONE;
        }
        if (data == null) {
            throw new BusinessException("A data para conversão de câmbio é obrigatória.");
        }

        String origem = moedaRecebivel.trim();
        String destino = moedaAlvo.trim();

        return taxaCambioRepository
                .findByMoedaOrigem_CodMoedaIgnoreCaseAndMoedaDestino_CodMoedaIgnoreCaseAndDataReferencia(
                        origem, destino, data)
                .map(TaxaCambio::getValor)
                .orElseThrow(() -> new BusinessException(String.format(
                        "Não há taxa de câmbio cadastrada de %s para %s na data %s.",
                        origem, destino, data)));
    }

    /**
     * Indica que não há conversão a realizar: quando a moeda alvo não foi informada ou é igual à
     * moeda do recebível (comparação case-insensitive).
     */
    private boolean semConversao(String moedaRecebivel, String moedaAlvo) {
        if (moedaAlvo == null || moedaAlvo.isBlank()) {
            return true;
        }
        if (moedaRecebivel == null || moedaRecebivel.isBlank()) {
            throw new BusinessException(
                    "A moeda do recebível é obrigatória para a conversão de câmbio.");
        }
        return moedaRecebivel.trim().equalsIgnoreCase(moedaAlvo.trim());
    }
}
