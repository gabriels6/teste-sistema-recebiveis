package com.gabriel.testesistemarecebiveis.precificacao.strategy;

import com.gabriel.testesistemarecebiveis.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Seleciona a {@link SpreadStrategy} adequada a partir do nome do tipo de recebível.
 *
 * <p>As estratégias disponíveis são descobertas automaticamente pelo Spring: basta declarar um novo
 * {@link SpreadStrategy} anotado com {@code @Component} para que ele passe a ser resolvido aqui, sem
 * alterar o motor de precificação.</p>
 */
@Component
public class SpreadStrategyResolver {

    private final Map<String, SpreadStrategy> strategiesByTipo;

    public SpreadStrategyResolver(List<SpreadStrategy> strategies) {
        this.strategiesByTipo = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> normalizar(strategy.tipoRecebivel()),
                        Function.identity()));
    }

    /**
     * Retorna a estratégia responsável pelo tipo de recebível informado.
     *
     * @throws BusinessException quando o tipo é vazio ou não possui estratégia cadastrada.
     */
    public SpreadStrategy resolver(String tipoRecebivel) {
        if (!StringUtils.hasText(tipoRecebivel)) {
            throw new BusinessException("O tipo de recebível é obrigatório para a precificação.");
        }
        SpreadStrategy strategy = strategiesByTipo.get(normalizar(tipoRecebivel));
        if (strategy == null) {
            throw new BusinessException(
                    "Não há estratégia de precificação para o tipo de recebível: " + tipoRecebivel + ".");
        }
        return strategy;
    }

    private String normalizar(String tipoRecebivel) {
        return tipoRecebivel.trim().toLowerCase(Locale.ROOT);
    }
}
