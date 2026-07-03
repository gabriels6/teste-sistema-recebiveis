-- Trilha de auditoria: um registro por operacao (criacao, atualizacao ou
-- remocao) de qualquer entidade, gravado de forma transversal pelos event
-- listeners do Hibernate. A coluna "dados" guarda, em JSON, a fotografia do
-- estado da entidade (para atualizacoes, os estados anterior e atual).
CREATE TABLE auditoria (
    id BIGSERIAL PRIMARY KEY,
    entidade VARCHAR(150) NOT NULL,
    entidade_id VARCHAR(100),
    operacao VARCHAR(20) NOT NULL,
    usuario VARCHAR(100),
    dados TEXT,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Consulta tipica: trilha de um registro especifico de uma entidade.
CREATE INDEX idx_auditoria_entidade ON auditoria (entidade, entidade_id);

-- Consulta por periodo / ordenacao temporal.
CREATE INDEX idx_auditoria_data_hora ON auditoria (data_hora);
