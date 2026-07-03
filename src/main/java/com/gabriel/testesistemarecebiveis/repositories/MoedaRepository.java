package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoedaRepository extends JpaRepository<Moeda, Integer> {

    /** Busca uma moeda pelo codigo, ignorando diferencas de caixa. */
    Optional<Moeda> findByCodMoedaIgnoreCase(String codMoeda);
}
