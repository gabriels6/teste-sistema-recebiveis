# Simulação de bug indo a produção

## Cenário

Um desenvolvedor devido a uma falta de entendimento completo da aplicação, colocou a data de liquidação como nullable false, sendo que por regra de negócio ela apenas é preenchida após liquidação da transação.

## Impacto

Operadores não conseguem criar novas transações de nenhuma forma, o que causa um travamento completo no fluxo dos clientes (Crítico).