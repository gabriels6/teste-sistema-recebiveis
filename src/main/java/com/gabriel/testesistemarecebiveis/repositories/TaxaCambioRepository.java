package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxaCambioRepository extends JpaRepository<TaxaCambio, Integer> {
}
