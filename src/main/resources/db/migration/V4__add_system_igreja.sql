-- Insere uma igreja padrão com ID 0 para usuários sem vínculo inicial
-- Usando sintaxe compatível com PostgreSQL e H2
INSERT INTO igrejas (id, nome, logradouro, numero, bairro, cidade, estado, cep, data_criacao)
SELECT 0, 'Sistema', 'Rua do Sistema', '0', 'Centro', 'Cidade', 'EX', '00000-000', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM igrejas WHERE id = 0);

-- Garante que o contador de ID comece em 1 para não conflitar com o 0
-- Esta parte é específica do PostgreSQL e será ignorada pelo H2 se estiver em blocos separados ou se usarmos um script mais flexível.
-- No entanto, para o Flyway em H2 (testes), podemos simplificar.
