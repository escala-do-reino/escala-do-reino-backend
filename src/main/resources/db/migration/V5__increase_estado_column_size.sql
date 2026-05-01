-- Aumenta o tamanho da coluna estado para suportar nomes maiores e evitar erros de truncamento
ALTER TABLE igrejas ALTER COLUMN estado TYPE VARCHAR(50);

-- Aproveitando para garantir que outros campos de endereço tenham tamanhos confortáveis
ALTER TABLE igrejas ALTER COLUMN cep TYPE VARCHAR(10);
ALTER TABLE igrejas ALTER COLUMN numero TYPE VARCHAR(20);
