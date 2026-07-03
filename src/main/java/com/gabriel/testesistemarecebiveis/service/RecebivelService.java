package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.Recebivel;
import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.CedenteRepository;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.RecebivelRepository;
import com.gabriel.testesistemarecebiveis.repositories.TipoRecebivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RecebivelService {

    private final RecebivelRepository recebivelRepository;
    private final MoedaRepository moedaRepository;
    private final CedenteRepository cedenteRepository;
    private final TipoRecebivelRepository tipoRecebivelRepository;

    public RecebivelService(RecebivelRepository recebivelRepository,
                            MoedaRepository moedaRepository,
                            CedenteRepository cedenteRepository,
                            TipoRecebivelRepository tipoRecebivelRepository) {
        this.recebivelRepository = recebivelRepository;
        this.moedaRepository = moedaRepository;
        this.cedenteRepository = cedenteRepository;
        this.tipoRecebivelRepository = tipoRecebivelRepository;
    }

    @Transactional(readOnly = true)
    public List<Recebivel> findAll() {
        return recebivelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Recebivel findById(Integer id) {
        return recebivelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recebível", id));
    }

    @Transactional
    public Recebivel create(Recebivel recebivel) {
        validate(recebivel);
        resolveRelations(recebivel);
        return recebivelRepository.save(recebivel);
    }

    @Transactional
    public Recebivel update(Integer id, Recebivel recebivel) {
        Recebivel existing = findById(id);
        validate(recebivel);
        resolveRelations(recebivel);
        existing.setMoeda(recebivel.getMoeda());
        existing.setCedente(recebivel.getCedente());
        existing.setTipoRecebivel(recebivel.getTipoRecebivel());
        existing.setCodAtivo(recebivel.getCodAtivo());
        existing.setDataVencimento(recebivel.getDataVencimento());
        existing.setTaxaBase(recebivel.getTaxaBase());
        return recebivelRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Recebivel existing = findById(id);
        recebivelRepository.delete(existing);
    }

    private void validate(Recebivel recebivel) {
        if (recebivel == null) {
            throw new BusinessException("Os dados do recebível são obrigatórios.");
        }
        if (recebivel.getMoeda() == null || recebivel.getMoeda().getId() == null) {
            throw new BusinessException("A moeda do recebível é obrigatória.");
        }
        if (recebivel.getCedente() == null || recebivel.getCedente().getId() == null) {
            throw new BusinessException("O cedente do recebível é obrigatório.");
        }
        if (recebivel.getTipoRecebivel() == null || recebivel.getTipoRecebivel().getId() == null) {
            throw new BusinessException("O tipo de recebível é obrigatório.");
        }
        if (!StringUtils.hasText(recebivel.getCodAtivo())) {
            throw new BusinessException("O código do ativo é obrigatório.");
        }
        if (recebivel.getDataVencimento() == null) {
            throw new BusinessException("A data de vencimento é obrigatória.");
        }
        if (recebivel.getTaxaBase() == null || recebivel.getTaxaBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("A taxa base deve ser maior ou igual a zero.");
        }
    }

    private void resolveRelations(Recebivel recebivel) {
        Moeda moeda = moedaRepository.findById(recebivel.getMoeda().getId())
                .orElseThrow(() -> new BusinessException("A moeda informada não existe."));
        Cedente cedente = cedenteRepository.findById(recebivel.getCedente().getId())
                .orElseThrow(() -> new BusinessException("O cedente informado não existe."));
        TipoRecebivel tipoRecebivel = tipoRecebivelRepository.findById(recebivel.getTipoRecebivel().getId())
                .orElseThrow(() -> new BusinessException("O tipo de recebível informado não existe."));
        recebivel.setMoeda(moeda);
        recebivel.setCedente(cedente);
        recebivel.setTipoRecebivel(tipoRecebivel);
    }
}
