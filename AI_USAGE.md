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

# EFICIÊNCIA IA

Prompt 1 economizou horas elaborando as entidades definidas a partir do modelo ER

# DIFICULDADES IA