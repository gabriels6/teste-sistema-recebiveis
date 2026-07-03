-- Usuário de teste para acesso inicial à aplicação.
--
-- ATENÇÃO: migration destinada apenas a habilitar o primeiro acesso em ambiente
-- de desenvolvimento/homologação. Deve ser REMOVIDA antes da subida em produção,
-- pois insere credenciais de conhecimento público.
--
-- Credenciais: nome = "admin" | senha = "admin123"
-- O hash abaixo foi gerado com BCrypt (força 10), compatível com o
-- BCryptPasswordEncoder configurado em SecurityConfig.

-- Função de apoio ao usuário de teste (a tabela funcao não possui carga inicial).
INSERT INTO funcao (nome)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM funcao WHERE nome = 'ADMIN');

-- Usuário de teste vinculado à função ADMIN.
INSERT INTO usuario (id_funcao, nome, hash_senha, tentativas)
SELECT (SELECT id FROM funcao WHERE nome = 'ADMIN'),
       'admin',
       '$2a$10$6DIznrdjsltLw6lpvza7yu0eWR.xO72LWHfs.styxL0tS3XP8S2.m',
       0
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE nome = 'admin');
