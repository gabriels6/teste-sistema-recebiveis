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

# Como executar localmente

Via docker: 
    - crie um arquivo .env com as configurações próprias do ambiente, conforme o .env.example
    - execute source .env
    - execute docker-compose up -d
    - para o front end, extraia o repositório https://github.com/gabriels6/teste-sistema-recebiveis-frontend, execute npm install para instalar as dependências e npm run start

# Possibilidade de execução em cenário de alta escala

Para atender uma necessidade de alta escala, de +1 milhão de transações/minuto, uma das abordagens possível é um cache de dados de retorno utilizando Redis, para dados estáticos por um período de tempo, com um TTL de 1 minuto. Já em casos de dados com possibilidade de mudança constante, como as transações, que tendem a acumular um volume maior durante o dia, adicionar workers também em Java que recebem essas transações e efetuam suas liquidações ou cálculos pendentes que requerem um processamento mais pesados, assim permitindo escalar verticalmente sem causar impacto na aplicação. Tais workers teriam apenas a função de verificar uma ou mais filas SQS, por exemplo, efetuar o processamento e salvar no banco de dados na sequências.

# Proposta Event Driven Architecture

Uma possibilidade de implementar o sistema com event driven architecture seria a implementação de filas com AWS SQS, com o recebimento de dados via API interna, lambda ou kafka, de acordo com os recursos disponíveis, para processamento assíncrono das transações e cadastro por integrações externas, adicionando na sequência um ou mais processadores que consomem dessa fila e executa a criação das entidades e efetua as liquidações e cálculo, que na sequência poderiam efetuar um trigger para retorno dos ativos precificados em outro tópico de retorno do kafka para os demais sistema interessados nesse consumo.