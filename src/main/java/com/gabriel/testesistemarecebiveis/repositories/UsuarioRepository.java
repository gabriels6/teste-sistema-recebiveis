package com.gabriel.testesistemarecebiveis.repositories;

import com.gabriel.testesistemarecebiveis.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
