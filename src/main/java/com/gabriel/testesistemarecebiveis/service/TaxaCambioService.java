package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.TaxaCambio;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.TaxaCambioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TaxaCambioService {

    private final TaxaCambioRepository taxaCambioRepository;
    private final MoedaRepository moedaRepository;

    public TaxaCambioService(TaxaCambioRepository taxaCambioRepository, MoedaRepository moedaRepository) {
        this.taxaCambioRepository = taxaCambioRepository;
        this.moedaRepository = moedaRepository;
    }

    @Transactional(readOnly = true)
    public List<TaxaCambio> findAll() {
        return taxaCambioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TaxaCambio findById(Integer id) {
        return taxaCambioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taxa de câmbio", id));
    }

    @Transactional
    public TaxaCambio create(TaxaCambio taxaCambio) {
        validate(taxaCambio);
        resolveMoedas(taxaCambio);
        return taxaCambioRepository.save(taxaCambio);
    }

    @Transactional
    public TaxaCambio update(Integer id, TaxaCambio taxaCambio) {
        TaxaCambio existing = findById(id);
        validate(taxaCambio);
        resolveMoedas(taxaCambio);
        existing.setMoedaOrigem(taxaCambio.getMoedaOrigem());
        existing.setMoedaDestino(taxaCambio.getMoedaDestino());
        existing.setDataReferencia(taxaCambio.getDataReferencia());
        existing.setValor(taxaCambio.getValor());
        return taxaCambioRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        TaxaCambio existing = findById(id);
        taxaCambioRepository.delete(existing);
    }

    private void validate(TaxaCambio taxaCambio) {
        if (taxaCambio == null) {
            throw new BusinessException("Os dados da taxa de câmbio são obrigatórios.");
        }
        if (taxaCambio.getMoedaOrigem() == null || taxaCambio.getMoedaOrigem().getId() == null) {
            throw new BusinessException("A moeda de origem é obrigatória.");
        }
        if (taxaCambio.getMoedaDestino() == null || taxaCambio.getMoedaDestino().getId() == null) {
            throw new BusinessException("A moeda de destino é obrigatória.");
        }
        if (taxaCambio.getDataReferencia() == null) {
            throw new BusinessException("A data de referência é obrigatória.");
        }
        if (taxaCambio.getValor() == null || taxaCambio.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor da taxa de câmbio deve ser maior que zero.");
        }
    }

    private void resolveMoedas(TaxaCambio taxaCambio) {
        Moeda origem = moedaRepository.findById(taxaCambio.getMoedaOrigem().getId())
                .orElseThrow(() -> new BusinessException("A moeda de origem informada não existe."));
        Moeda destino = moedaRepository.findById(taxaCambio.getMoedaDestino().getId())
                .orElseThrow(() -> new BusinessException("A moeda de destino informada não existe."));
        taxaCambio.setMoedaOrigem(origem);
        taxaCambio.setMoedaDestino(destino);
    }
}
