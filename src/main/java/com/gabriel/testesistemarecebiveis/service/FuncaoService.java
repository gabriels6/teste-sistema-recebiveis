package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FuncaoService {

    private final FuncaoRepository funcaoRepository;

    public FuncaoService(FuncaoRepository funcaoRepository) {
        this.funcaoRepository = funcaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Funcao> findAll() {
        return funcaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Funcao findById(Integer id) {
        return funcaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Função", id));
    }

    @Transactional
    public Funcao create(Funcao funcao) {
        validate(funcao);
        return funcaoRepository.save(funcao);
    }

    @Transactional
    public Funcao update(Integer id, Funcao funcao) {
        Funcao existing = findById(id);
        validate(funcao);
        existing.setNome(funcao.getNome());
        return funcaoRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Funcao existing = findById(id);
        funcaoRepository.delete(existing);
    }

    private void validate(Funcao funcao) {
        if (funcao == null) {
            throw new BusinessException("Os dados da função são obrigatórios.");
        }
        if (!StringUtils.hasText(funcao.getNome())) {
            throw new BusinessException("O nome da função é obrigatório.");
        }
    }
}
