package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TaxaCambioRepository extends JpaRepository<TaxaCambio, Integer> {

    /**
     * Busca a taxa de câmbio da moeda de origem para a moeda de destino em uma data de referência.
     * A comparação dos códigos de moeda é feita de forma case-insensitive.
     */
    Optional<TaxaCambio> findByMoedaOrigem_CodMoedaIgnoreCaseAndMoedaDestino_CodMoedaIgnoreCaseAndDataReferencia(
            String codMoedaOrigem, String codMoedaDestino, LocalDate dataReferencia);
}
