-- Execute apenas se quiser remover os registros de demonstração que a versão anterior
-- do sistema inseriu automaticamente. Faça um backup antes.
USE sigpesq;
START TRANSACTION;

DELETE FROM autoria
WHERE id_producao IN (
    SELECT id_producao
    FROM producao
    WHERE titulo IN (
        'Deep Learning for Clinical Predictions',
        'Benchmarking Distributed Query Optimizers',
        'Relatório Técnico de Validação dos Modelos Preditivos'
    )
);

DELETE FROM producao
WHERE titulo IN (
    'Deep Learning for Clinical Predictions',
    'Benchmarking Distributed Query Optimizers',
    'Relatório Técnico de Validação dos Modelos Preditivos'
);

DELETE FROM financiamento
WHERE id_projeto IN (
    SELECT id_projeto
    FROM projeto
    WHERE titulo IN (
        'Inteligência Artificial Aplicada à Saúde',
        'Otimização de Bancos de Dados Distribuídos',
        'Segurança da Informação e Criptografia Quântica'
    )
);

DELETE FROM historico_proj
WHERE id_projeto IN (
    SELECT id_projeto
    FROM projeto
    WHERE titulo IN (
        'Inteligência Artificial Aplicada à Saúde',
        'Otimização de Bancos de Dados Distribuídos',
        'Segurança da Informação e Criptografia Quântica'
    )
);

DELETE FROM projeto
WHERE titulo IN (
    'Inteligência Artificial Aplicada à Saúde',
    'Otimização de Bancos de Dados Distribuídos',
    'Segurança da Informação e Criptografia Quântica'
);

-- Exclui apenas os participantes de demonstração sem vínculos remanescentes.
DELETE FROM participante
WHERE cpf_colaborador IN (
    '52998224725', '12345678909', '98765432100',
    '23049582092', '85940381045', '47291840291'
)
AND NOT EXISTS (
    SELECT 1 FROM historico_proj h
    WHERE h.cpf_colaborador = participante.cpf_colaborador
)
AND NOT EXISTS (
    SELECT 1 FROM autoria a
    WHERE a.cpf_particip = participante.cpf_colaborador
);

-- Remove os perfis de demonstração somente se não coordenarem outros projetos.
DELETE FROM coordenador
WHERE cpf IN ('52998224725', '12345678909', '98765432100')
AND NOT EXISTS (
    SELECT 1 FROM projeto p
    WHERE p.cpf_coordenador = coordenador.cpf
);

DELETE FROM usuario
WHERE email_institucional IN (
    'carlos.eduardo@ufes.br',
    'mariana.ferreira@ufes.br',
    'roberto.almeida@ufes.br',
    'gestor@ufes.br'
)
AND NOT EXISTS (
    SELECT 1 FROM coordenador c
    WHERE c.cpf = usuario.cpf
);

COMMIT;
