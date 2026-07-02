# LEIA-ME

# SOBRE

O projeto atual se refere a API de um sistema de controle e precificação de recebíveis.

# STACK

Banco:
    - Postgres

Aplicação: 
    - Spring boot 4
    - Java 17

# CONFIGURAÇÃO PARA DESENVOLVIMENTO

Para configurar corretamente o ambiente, é necessário ter, além da stack mencionada acima:
    - pre-commit (hooks de linter)
    - Linter java (https://github.com/cristianoliveira/java-checkstyle)
    - Docker (para subida dos container do banco de dados e aplicação)

# ACESSO INICIAL

Para o primeiro acesso, a migration `V4__insert_usuario_teste.sql` cria um usuário de teste:

    - Usuário: admin
    - Senha:   admin123

O login é feito via `POST /api/auth/login`, enviando `{ "nome": "admin", "senha": "admin123" }`, que retorna um token JWT a ser usado nas demais requisições.

> ⚠️ **ATENÇÃO:** Este usuário existe apenas para viabilizar o primeiro acesso em ambiente de desenvolvimento. A migration `V4__insert_usuario_teste.sql` deve ser **removida** (e o usuário excluído do banco) antes da subida em produção, pois utiliza credenciais de conhecimento público.

# FLUXO DE DESENVOLVIMENTO UTILIZADO

Foi utilizado o Git Flow, devido a familiaridade do desenvolvedor com o fluxo e a possibilidade de centralizar commits de mais de um desenvolvedor na branch intermediária de desenvolvimento (development), montando na sequência as releases quando o código se encontra estável e pronto para subida em produção, e a possibilidade de paralelizar desenvolvimentos distintos (motor de precificação e APIs de CRUD, por exemplo).

