# PROMPTS

1. Implemente a estrutura inicial do projeto, com migrations Flyway, entities e JPA Repositories, tendo a seguinte estrutura de banco de dados:

Tabela: Funcao
Campos:
    PK  id integer
        nome  string

Tabela: Usuario
Campos:
    PK id integer
    FK id_funcao integer referencia Funcao(id)
        nome string
        hash_senha: string
        tentativas: integer

Tabela: Tipo_Recebivel
Campos:
    PK id integer
        nome string

Tabela: Cedente
Campos:
    PK id integer
        cod_empresa string
        nome string

Tabela: Moeda
Campos:
    PK id integer
        cod_moeda string


Tabela: Taxa_Cambio
Campos:
    PK id integer
    FK id_moeda_origem integer referencia Moeda(id)
    FK id_moeda_destino integer referencia Moeda(id)
        data_referencia date
        valor bigdecimal(10,8)


Tabela: Recebivel
Campos:
    PK id integer
    FK id_moeda integer referencia Moeda(id)
    FK id_cedente integer referencia Cedente(id)
    FK id_tipo_recebivel integer referencia Tipo_Recebivel(id)
        cod_ativo string
        data_vencimento date
        spread bigdecimal(10,8)

Tabela: Transacao
Campos:
    PK id integer
    FK id_usuario integer referencia Usuario(id)
    FK id_recebivel integer referencia Recebivel(id)
    FK id_moeda integer referencia Moeda(id)
    data_operacao date
    data_liquidacao date nullable
    qtde_operacao bigdecimal(10,8)

O banco utilizado será PostgreSQL. Organize o projeto tendo todas as entidades na pasta entities, e todos os repositórios na pasta repositories

2. Efetue as seguintes etapas:
    a. Para o login, será feito por JWT. Utilize BCrypt para hashing de hash_senha do usuário e validação posterior
    b. Implemente o service e controller de autenticação no formato JWT, para login no sistema. O sistema irá funcionar no modelo cliente servidor, a partir de APIs REST, no formato bearer token, para todos os endpoints, exceto aqueles que requerem autenticação (não implemente esses endpoints até o momento)
    c. Implemente um bloqueio de usuário após 3 tentativas falhas, e traga as tentativas restantes. Traga mensagens explicativas em caso de erros
    d. Crie testes unitários para cada classe criada de controller ou serviço

3. Implemente as APIs de CRUD para cada uma das entidades, contendo mensagens claras em caso de erros de validação, retornos e payloads padronizados, e seguindo o padrão REST. Implemente também uma documentação dos endpoints utilizando swagger que requeira o mínimo ou nenhuma intervenção manual para atualização da documentação. Elabore a estrutura passando pelos serviços para validação das entidades e inserção/modificação/remoção. Não adicione lógicas de negócio nos controllers, apenas nos services.

Implemente também testes unitários e garanta e funcionamento deles.

# EFICIÊNCIA IA

Prompt 1 economizou horas elaborando as entidades definidas a partir do modelo ER

# DIFICULDADES IA

Ao criar os testes unitários, IA utilizou recursos que não estavam mais presentes nas libs. Resolvido com prompt:

Estou com problemas ao executar os testes. Algumas das classes passadas como 

```
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.mock.mockito.MockBean;
```

Não foram identificadas.