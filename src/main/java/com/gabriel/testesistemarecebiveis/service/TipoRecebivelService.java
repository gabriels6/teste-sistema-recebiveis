package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.TipoRecebivel;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.TipoRecebivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TipoRecebivelService {

    private final TipoRecebivelRepository tipoRecebivelRepository;

    public TipoRecebivelService(TipoRecebivelRepository tipoRecebivelRepository) {
        this.tipoRecebivelRepository = tipoRecebivelRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoRecebivel> findAll() {
        return tipoRecebivelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TipoRecebivel findById(Integer id) {
        return tipoRecebivelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de recebível", id));
    }

    @Transactional
    public TipoRecebivel create(TipoRecebivel tipoRecebivel) {
        validate(tipoRecebivel);
        return tipoRecebivelRepository.save(tipoRecebivel);
    }

    @Transactional
    public TipoRecebivel update(Integer id, TipoRecebivel tipoRecebivel) {
        TipoRecebivel existing = findById(id);
        validate(tipoRecebivel);
        existing.setNome(tipoRecebivel.getNome());
        return tipoRecebivelRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        TipoRecebivel existing = findById(id);
        tipoRecebivelRepository.delete(existing);
    }

    private void validate(TipoRecebivel tipoRecebivel) {
        if (tipoRecebivel == null) {
            throw new BusinessException("Os dados do tipo de recebível são obrigatórios.");
        }
        if (!StringUtils.hasText(tipoRecebivel.getNome())) {
            throw new BusinessException("O nome do tipo de recebível é obrigatório.");
        }
    }
}
