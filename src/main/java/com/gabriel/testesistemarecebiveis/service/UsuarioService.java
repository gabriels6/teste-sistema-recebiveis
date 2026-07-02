package com.gabriel.testesistemarecebiveis.service;

import com.gabriel.testesistemarecebiveis.entities.Funcao;
import com.gabriel.testesistemarecebiveis.entities.Usuario;
import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import com.gabriel.testesistemarecebiveis.exception.ResourceNotFoundException;
import com.gabriel.testesistemarecebiveis.repositories.FuncaoRepository;
import com.gabriel.testesistemarecebiveis.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          FuncaoRepository funcaoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.funcaoRepository = funcaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    @Transactional
    public Usuario create(Usuario usuario) {
        validate(usuario);
        if (usuario.getFuncao() != null && usuario.getFuncao().getId() != null) {
            Funcao funcao = funcaoRepository.findById(usuario.getFuncao().getId())
                    .orElseThrow(() -> new BusinessException("A função informada não existe."));
            usuario.setFuncao(funcao);
        }
        usuario.setHashSenha(passwordEncoder.encode(usuario.getHashSenha()));
        usuario.setTentativas(0);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Integer id, Usuario usuario) {
        Usuario existing = findById(id);
        validate(usuario);
        if (usuario.getFuncao() != null && usuario.getFuncao().getId() != null) {
            Funcao funcao = funcaoRepository.findById(usuario.getFuncao().getId())
                    .orElseThrow(() -> new BusinessException("A função informada não existe."));
            existing.setFuncao(funcao);
        }
        existing.setNome(usuario.getNome());
        if (StringUtils.hasText(usuario.getHashSenha())) {
            existing.setHashSenha(passwordEncoder.encode(usuario.getHashSenha()));
        }
        existing.setTentativas(usuario.getTentativas() == null ? 0 : usuario.getTentativas());
        return usuarioRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Usuario existing = findById(id);
        usuarioRepository.delete(existing);
    }

    private void validate(Usuario usuario) {
        if (usuario == null) {
            throw new BusinessException("Os dados do usuário são obrigatórios.");
        }
        if (usuario.getFuncao() == null || usuario.getFuncao().getId() == null) {
            throw new BusinessException("A função do usuário é obrigatória.");
        }
        if (!StringUtils.hasText(usuario.getNome())) {
            throw new BusinessException("O nome do usuário é obrigatório.");
        }
        if (!StringUtils.hasText(usuario.getHashSenha())) {
            throw new BusinessException("A senha do usuário é obrigatória.");
        }
    }
}
