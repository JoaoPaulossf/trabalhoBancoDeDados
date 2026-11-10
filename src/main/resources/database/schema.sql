CREATE DATABASE IF NOT EXISTS sigpesq
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sigpesq;

-- A senha deve ser armazenada como hash, nunca em texto puro.
CREATE TABLE IF NOT EXISTS usuario (
    cpf CHAR(11) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    email_institucional VARCHAR(254) NOT NULL,
    nivel_acesso ENUM('Coordenador de Projeto', 'Gestor Administrativo') NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (cpf),
    UNIQUE KEY uk_usuario_email (email_institucional)
) ENGINE = InnoDB;

-- Subtipo de usuário que pode coordenar projetos.
CREATE TABLE IF NOT EXISTS coordenador (
    cpf CHAR(11) NOT NULL,
    PRIMARY KEY (cpf),
    CONSTRAINT fk_coordenador_usuario
        FOREIGN KEY (cpf) REFERENCES usuario (cpf)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS participante (
    cpf_colaborador CHAR(11) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    tipo ENUM('Docente', 'Discente', 'Técnico-Administrativo') NOT NULL,
    PRIMARY KEY (cpf_colaborador),
    UNIQUE KEY uk_participante_email (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS projeto (
    id_projeto INT NOT NULL AUTO_INCREMENT,
    cpf_coordenador CHAR(11) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_termino DATE,
    situacao ENUM('Em andamento', 'Concluído', 'Cancelado') NOT NULL DEFAULT 'Em andamento',
    PRIMARY KEY (id_projeto),
    KEY idx_projeto_coordenador (cpf_coordenador),
    CONSTRAINT fk_projeto_coordenador
        FOREIGN KEY (cpf_coordenador) REFERENCES coordenador (cpf)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_projeto_datas
        CHECK (data_termino IS NULL OR data_termino >= data_inicio)
) ENGINE = InnoDB;

-- Relação N:N entre projeto e participante; o par forma a chave primária.
CREATE TABLE IF NOT EXISTS historico_proj (
    id_projeto INT NOT NULL,
    cpf_colaborador CHAR(11) NOT NULL,
    funcao VARCHAR(100) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    PRIMARY KEY (id_projeto, cpf_colaborador),
    KEY idx_historico_participante (cpf_colaborador),
    CONSTRAINT fk_historico_projeto
        FOREIGN KEY (id_projeto) REFERENCES projeto (id_projeto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_historico_participante
        FOREIGN KEY (cpf_colaborador) REFERENCES participante (cpf_colaborador)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_historico_datas
        CHECK (data_fim IS NULL OR data_fim >= data_inicio)
) ENGINE = InnoDB;

-- O ID do projeto como PK garante no máximo um financiamento por projeto.
CREATE TABLE IF NOT EXISTS financiamento (
    id_projeto INT NOT NULL,
    agencia VARCHAR(120) NOT NULL,
    tipo_fomento VARCHAR(120) NOT NULL,
    valor_fomento DECIMAL(12, 2) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    situacao ENUM('Ativo', 'Inativo') NOT NULL DEFAULT 'Ativo',
    PRIMARY KEY (id_projeto),
    CONSTRAINT fk_financiamento_projeto
        FOREIGN KEY (id_projeto) REFERENCES projeto (id_projeto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_financiamento_valor CHECK (valor_fomento >= 0),
    CONSTRAINT chk_financiamento_datas CHECK (data_fim >= data_inicio)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS producao (
    id_producao INT NOT NULL AUTO_INCREMENT,
    id_projeto INT NOT NULL,
    titulo VARCHAR(250) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    data_publicacao VARCHAR(20) NOT NULL,
    meio_divulgacao VARCHAR(200),
    PRIMARY KEY (id_producao),
    KEY idx_producao_projeto (id_projeto),
    CONSTRAINT fk_producao_projeto
        FOREIGN KEY (id_projeto) REFERENCES projeto (id_projeto)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Relação N:N entre produção científica e participante autor.
CREATE TABLE IF NOT EXISTS autoria (
    id_producao INT NOT NULL,
    cpf_particip CHAR(11) NOT NULL,
    PRIMARY KEY (id_producao, cpf_particip),
    KEY idx_autoria_participante (cpf_particip),
    CONSTRAINT fk_autoria_producao
        FOREIGN KEY (id_producao) REFERENCES producao (id_producao)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_autoria_participante
        FOREIGN KEY (cpf_particip) REFERENCES participante (cpf_colaborador)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;