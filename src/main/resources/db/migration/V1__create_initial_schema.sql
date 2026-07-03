CREATE TABLE funcao (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    id_funcao INTEGER NOT NULL,
    nome VARCHAR(100) NOT NULL,
    hash_senha VARCHAR(255) NOT NULL,
    tentativas INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_usuario_funcao FOREIGN KEY (id_funcao) REFERENCES funcao(id)
);

CREATE TABLE tipo_recebivel (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE cedente (
    id SERIAL PRIMARY KEY,
    cod_empresa VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE moeda (
    id SERIAL PRIMARY KEY,
    cod_moeda VARCHAR(10) NOT NULL
);

CREATE TABLE taxa_cambio (
    id SERIAL PRIMARY KEY,
    id_moeda_origem INTEGER NOT NULL,
    id_moeda_destino INTEGER NOT NULL,
    data_referencia DATE NOT NULL,
    valor NUMERIC(10,8) NOT NULL,
    CONSTRAINT fk_taxa_origem FOREIGN KEY (id_moeda_origem) REFERENCES moeda(id),
    CONSTRAINT fk_taxa_destino FOREIGN KEY (id_moeda_destino) REFERENCES moeda(id)
);

CREATE TABLE recebivel (
    id SERIAL PRIMARY KEY,
    id_moeda INTEGER NOT NULL,
    id_cedente INTEGER NOT NULL,
    id_tipo_recebivel INTEGER NOT NULL,
    cod_ativo VARCHAR(50) NOT NULL,
    data_vencimento DATE NOT NULL,
    spread NUMERIC(10,8) NOT NULL,
    CONSTRAINT fk_recebivel_moeda FOREIGN KEY (id_moeda) REFERENCES moeda(id),
    CONSTRAINT fk_recebivel_cedente FOREIGN KEY (id_cedente) REFERENCES cedente(id),
    CONSTRAINT fk_recebivel_tipo FOREIGN KEY (id_tipo_recebivel) REFERENCES tipo_recebivel(id)
);

CREATE TABLE transacao (
    id SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    id_recebivel INTEGER NOT NULL,
    id_moeda INTEGER NOT NULL,
    data_operacao DATE NOT NULL,
    data_liquidacao DATE,
    qtde_operacao NUMERIC(10,8) NOT NULL,
    CONSTRAINT fk_transacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_transacao_recebivel FOREIGN KEY (id_recebivel) REFERENCES recebivel(id),
    CONSTRAINT fk_transacao_moeda FOREIGN KEY (id_moeda) REFERENCES moeda(id)
);

-- Filtro por range de data de liquidação + desempate por id, casando com a ordenação do relatório
-- (data_liquidacao DESC, id DESC). NULLS LAST no índice espelha o ORDER BY para permitir varredura
-- ordenada sem sort adicional.
CREATE INDEX IF NOT EXISTS idx_transacao_data_liquidacao
    ON transacao (data_liquidacao DESC NULLS LAST, id DESC);

-- Junções a partir de transacao.
CREATE INDEX IF NOT EXISTS idx_transacao_id_recebivel ON transacao (id_recebivel);
CREATE INDEX IF NOT EXISTS idx_transacao_id_moeda ON transacao (id_moeda);

-- Junção recebivel -> cedente / tipo_recebivel.
CREATE INDEX IF NOT EXISTS idx_recebivel_id_cedente ON recebivel (id_cedente);

-- Filtro por nome do cedente com correspondência parcial (ILIKE '%termo%'). O índice de trigramas
-- (GIN) do pg_trgm acelera buscas por substring, que um índice B-tree comum não cobre.
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS idx_cedente_nome_trgm ON cedente USING gin (nome gin_trgm_ops);
