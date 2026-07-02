-- Preco unitario da operacao na transacao. NOT NULL com DEFAULT 0 para
-- preencher as linhas existentes; novos registros informam o valor via API
-- (validado como maior que zero no TransacaoService).
ALTER TABLE transacao ADD COLUMN preco_unitario NUMERIC(10,8) NOT NULL DEFAULT 0;
