package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Moeda;
import com.gabriel.testesistemarecebiveis.entities.Recebivel;
import com.gabriel.testesistemarecebiveis.entities.Transacao;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.MoedaRepository;
import com.gabriel.testesistemarecebiveis.repositories.RecebivelRepository;
import com.gabriel.testesistemarecebiveis.repositories.TransacaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecebivelRepository recebivelRepository;
    private final MoedaRepository moedaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            UsuarioRepository usuarioRepository,
                            RecebivelRepository recebivelRepository,
                            MoedaRepository moedaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recebivelRepository = recebivelRepository;
        this.moedaRepository = moedaRepository;
    }

    @Transactional(readOnly = true)
    public List<Transacao> findAll() {
        return transacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Transacao findById(Integer id) {
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", id));
    }

    @Transactional
    public Transacao create(Transacao transacao) {
        validate(transacao);
        resolveRelations(transacao);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao update(Integer id, Transacao transacao) {
        Transacao existing = findById(id);
        validate(transacao);
        resolveRelations(transacao);
        existing.setUsuario(transacao.getUsuario());
        existing.setRecebivel(transacao.getRecebivel());
        existing.setMoeda(transacao.getMoeda());
        existing.setDataOperacao(transacao.getDataOperacao());
        existing.setDataLiquidacao(transacao.getDataLiquidacao());
        existing.setQtdeOperacao(transacao.getQtdeOperacao());
        return transacaoRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Transacao existing = findById(id);
        transacaoRepository.delete(existing);
    }

    private void validate(Transacao transacao) {
        if (transacao == null) {
            throw new BusinessException("Os dados da transação são obrigatórios.");
        }
        if (transacao.getUsuario() == null || transacao.getUsuario().getId() == null) {
            throw new BusinessException("O usuário da transação é obrigatório.");
        }
        if (transacao.getRecebivel() == null || transacao.getRecebivel().getId() == null) {
            throw new BusinessException("O recebível da transação é obrigatório.");
        }
        if (transacao.getMoeda() == null || transacao.getMoeda().getId() == null) {
            throw new BusinessException("A moeda da transação é obrigatória.");
        }
        if (transacao.getDataOperacao() == null) {
            throw new BusinessException("A data da operação é obrigatória.");
        }
        if (transacao.getQtdeOperacao() == null || transacao.getQtdeOperacao().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A quantidade da operação deve ser maior que zero.");
        }
    }

    private void resolveRelations(Transacao transacao) {
        Usuario usuario = usuarioRepository.findById(transacao.getUsuario().getId())
                .orElseThrow(() -> new BusinessException("O usuário informado não existe."));
        Recebivel recebivel = recebivelRepository.findById(transacao.getRecebivel().getId())
                .orElseThrow(() -> new BusinessException("O recebível informado não existe."));
        Moeda moeda = moedaRepository.findById(transacao.getMoeda().getId())
                .orElseThrow(() -> new BusinessException("A moeda informada não existe."));
        transacao.setUsuario(usuario);
        transacao.setRecebivel(recebivel);
        transacao.setMoeda(moeda);
    }
}
