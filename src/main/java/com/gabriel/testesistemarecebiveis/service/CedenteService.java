package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Cedente;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.CedenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CedenteService {

    private final CedenteRepository cedenteRepository;

    public CedenteService(CedenteRepository cedenteRepository) {
        this.cedenteRepository = cedenteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cedente> findAll() {
        return cedenteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cedente findById(Integer id) {
        return cedenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cedente", id));
    }

    @Transactional
    public Cedente create(Cedente cedente) {
        validate(cedente);
        return cedenteRepository.save(cedente);
    }

    @Transactional
    public Cedente update(Integer id, Cedente cedente) {
        Cedente existing = findById(id);
        validate(cedente);
        existing.setCodEmpresa(cedente.getCodEmpresa());
        existing.setNome(cedente.getNome());
        return cedenteRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Cedente existing = findById(id);
        cedenteRepository.delete(existing);
    }

    private void validate(Cedente cedente) {
        if (cedente == null) {
            throw new BusinessException("Os dados do cedente são obrigatórios.");
        }
        if (!StringUtils.hasText(cedente.getCodEmpresa())) {
            throw new BusinessException("O código da empresa é obrigatório.");
        }
        if (!StringUtils.hasText(cedente.getNome())) {
            throw new BusinessException("O nome do cedente é obrigatório.");
        }
    }
}
