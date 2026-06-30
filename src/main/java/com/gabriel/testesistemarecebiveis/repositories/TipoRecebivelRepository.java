package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRecebivelRepository extends JpaRepository<TipoRecebivel, Integer> {
}
