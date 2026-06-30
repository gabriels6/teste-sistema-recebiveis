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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RepositoryStructureTest {

    @Autowired
    private FuncaoRepository funcaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoRecebivelRepository tipoRecebivelRepository;

    @Autowired
    private CedenteRepository cedenteRepository;

    @Autowired
    private MoedaRepository moedaRepository;

    @Autowired
    private TaxaCambioRepository taxaCambioRepository;

    @Autowired
    private RecebivelRepository recebivelRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Test
    void repositoriesAreAvailable() {
        assertThat(funcaoRepository).isNotNull();
        assertThat(usuarioRepository).isNotNull();
        assertThat(tipoRecebivelRepository).isNotNull();
        assertThat(cedenteRepository).isNotNull();
        assertThat(moedaRepository).isNotNull();
        assertThat(taxaCambioRepository).isNotNull();
        assertThat(recebivelRepository).isNotNull();
        assertThat(transacaoRepository).isNotNull();
    }
}
