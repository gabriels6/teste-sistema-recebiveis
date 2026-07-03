package com.gabriel.testesistemarecebiveis;

import com.gabriel.testesistemarecebiveis.repositories.CedenteRepository;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.RecebivelRepository;
import com.gabriel.testesistemarecebiveis.repositories.TaxaCambioRepository;
import com.gabriel.testesistemarecebiveis.repositories.TipoRecebivelRepository;
import com.gabriel.testesistemarecebiveis.repositories.TransacaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryStructureTest {

    @Test
    void repositoriesAreAvailable() {
        assertThat(FuncaoRepository.class).isNotNull();
        assertThat(UsuarioRepository.class).isNotNull();
        assertThat(TipoRecebivelRepository.class).isNotNull();
        assertThat(CedenteRepository.class).isNotNull();
        assertThat(MoedaRepository.class).isNotNull();
        assertThat(TaxaCambioRepository.class).isNotNull();
        assertThat(RecebivelRepository.class).isNotNull();
        assertThat(TransacaoRepository.class).isNotNull();
    }
}
