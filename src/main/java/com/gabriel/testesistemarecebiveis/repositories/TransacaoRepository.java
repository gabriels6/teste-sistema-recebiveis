package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
}
