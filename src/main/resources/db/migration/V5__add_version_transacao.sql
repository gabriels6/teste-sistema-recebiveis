-- Coluna de versao para optimistic locking (@Version) na tabela transacao.
-- Garante que alteracoes concorrentes (ex.: liquidacao vs. edicao) nao se
-- sobrescrevam silenciosamente: a segunda escrita sobre a mesma versao falha.
ALTER TABLE transacao ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
