-- Amplia a precisao dos campos decimais de NUMERIC(10,8) para NUMERIC(18,8),
-- permitindo ate 10 digitos inteiros (ex.: 1000000000.00000000). A escala de 8
-- casas decimais e mantida. O aumento de precisao preserva os dados existentes.
ALTER TABLE taxa_cambio ALTER COLUMN valor TYPE NUMERIC(18,8);
ALTER TABLE recebivel ALTER COLUMN taxa_base TYPE NUMERIC(18,8);
ALTER TABLE transacao ALTER COLUMN qtde_operacao TYPE NUMERIC(18,8);
