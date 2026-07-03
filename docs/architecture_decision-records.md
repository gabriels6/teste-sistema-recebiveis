# Decisões arquiteturas tomadas e justificativas

O presente documento visa explicitar a arquitetura escolhida e justificar as decisões tomadas

# Aplicação

A aplicação foi desenvolvida em Java 17, utilizando Spring Boot. A escolha da linguagem se deu por ela já estar consolidada no mercado, principalmente em softwares de cálculo, alinhado também a forte tipagem, frameworks e dependências já consolidadas, que garantem uma robustez maior ao sistema, além da possibilidade de execução multiplataforma, nativa do Java através da JVM. Já o Spring Boot foi optado por uma questão de velocidade de desenvolvimento, dado que é um framework com muitas facilidades que permitem desenvolver em um período menor de tempo sem um esforço muito alto, e com recursos prontos, como o de segurança com Spring Security, Spring Web, JPA, etc.

# Banco de dados

A estrutura de banco de dados escolhida foi SQL, utilizando o SGBD PostgreSQL. A escolha do banco de dados relacional SQL se deu pela sua robustez, além da necessidade de relacionamento entre entidades, como explicitado no diagrama_er.png, além da possibilidade desse tipo de banco seguir os princípios do ACID. A escolha do banco PostgreSQL se deu alinhado com a proposta de ser um banco já consolidado no mercado, de fácil integração com a stack escolhida para desenvolvimento da aplicação.

# Front end

O Front end da aplicação foi feito utilizando React, devido a ser um framework familiar ao desenvolvedor do projeto, o que permite ao front end ser feito de forma robusta. A escolha de bootstrap para a componentização se deu por uma necessidade na velocidade de desenvolvimento.