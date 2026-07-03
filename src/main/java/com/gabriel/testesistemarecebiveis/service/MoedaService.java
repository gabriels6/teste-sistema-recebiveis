package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MoedaService {

    private final MoedaRepository moedaRepository;

    public MoedaService(MoedaRepository moedaRepository) {
        this.moedaRepository = moedaRepository;
    }

    @Transactional(readOnly = true)
    public List<Moeda> findAll() {
        return moedaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Moeda findById(Integer id) {
        return moedaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moeda", id));
    }

    @Transactional
    public Moeda create(Moeda moeda) {
        validate(moeda);
        return moedaRepository.save(moeda);
    }

    @Transactional
    public Moeda update(Integer id, Moeda moeda) {
        Moeda existing = findById(id);
        validate(moeda);
        existing.setCodMoeda(moeda.getCodMoeda());
        return moedaRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Moeda existing = findById(id);
        moedaRepository.delete(existing);
    }

    private void validate(Moeda moeda) {
        if (moeda == null) {
            throw new BusinessException("Os dados da moeda são obrigatórios.");
        }
        if (!StringUtils.hasText(moeda.getCodMoeda())) {
            throw new BusinessException("O código da moeda é obrigatório.");
        }
    }
}
