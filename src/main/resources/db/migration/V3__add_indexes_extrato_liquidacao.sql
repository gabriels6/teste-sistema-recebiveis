-- Índices de apoio ao relatório de Extrato de Liquidação.
--
-- O relatório filtra por range de data de operação e/ou de liquidação, por nome do cedente e por
-- moeda da operação, com ordenação por data de liquidação. Para responder em segundos sobre milhões
-- de transações, cada coluna de filtro/junção/ordenação abaixo recebe um índice dedicado.

-- Filtro por range de data de operação (e ordenação secundária).
CREATE INDEX IF NOT EXISTS idx_transacao_data_operacao ON transacao (data_operacao);

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
