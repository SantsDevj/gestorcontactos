-- Remover base de dados se existir
DROP DATABASE IF EXISTS sistema_gerenciamento_contactos;

-- Criar base de dados
CREATE DATABASE sistema_gerenciamento_contactos
ON PRIMARY
(
    NAME = 'sistema_gerenciamento_contactos_Data',
    FILENAME = 'C:\SQLDATA\sistema_gerenciamento_contactos_Data.mdf',
    SIZE = 10MB,
    MAXSIZE = 500MB,
    FILEGROWTH = 10%
)
LOG ON
(
    NAME = 'sistema_gerenciamento_contactos_Log',
    FILENAME = 'C:\SQLDATA\sistema_gerenciamento_contactos_Log.ldf',
    SIZE = 5MB,
    MAXSIZE = 100MB,
    FILEGROWTH = 10%
);

USE sistema_gerenciamento_contactos;
GO

-- Criar schema
CREATE SCHEMA manager;
GO

-- ============================================
-- TABELA: utilizador
-- ============================================
CREATE TABLE manager.utilizador (
    id_utilizador INT NOT NULL IDENTITY(1,1),
    nome          VARCHAR(255),
    username      VARCHAR(100) NOT NULL,
    senha         CHAR(8)      NOT NULL,
    data_criacao  DATE,

    CONSTRAINT PK_utilizador         PRIMARY KEY (id_utilizador),
    CONSTRAINT UQ_utilizador_username UNIQUE (username)
);

-- ============================================
-- TABELA: categoria
-- ============================================
CREATE TABLE manager.categoria (
    id_categoria INT          NOT NULL IDENTITY(1,1),
    nome         VARCHAR(255) NOT NULL,
    descricao    VARCHAR(255),

    CONSTRAINT PK_categoria PRIMARY KEY (id_categoria)
);

-- ============================================
-- TABELA: contacto
-- ============================================
-- id_categoria é NULL para suportar ON DELETE SET NULL:
-- se uma categoria for eliminada, o contacto não é eliminado,
-- apenas perde a associação (id_categoria fica NULL)
CREATE TABLE manager.contacto (
    id_contacto   INT          NOT NULL IDENTITY(1,1),
    id_utilizador INT          NOT NULL,
    id_categoria  INT          NULL,
    nome_completo VARCHAR(255) NOT NULL,
    email         VARCHAR(255),
    endereco      VARCHAR(255),
    data_nasc     DATE,

    CONSTRAINT PK_contacto    PRIMARY KEY (id_contacto),

    CONSTRAINT FK_contacto_utilizador
        FOREIGN KEY (id_utilizador)
        REFERENCES manager.utilizador (id_utilizador)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_contacto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES manager.categoria (id_categoria)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ============================================
-- TABELA: telefone
-- ============================================
CREATE TABLE manager.telefone (
    id_telefone INT     NOT NULL IDENTITY(1,1),
    id_contacto INT     NOT NULL,
    numero      CHAR(9) NOT NULL,
    tipo        VARCHAR(100),

    CONSTRAINT PK_telefone PRIMARY KEY (id_telefone),

    CONSTRAINT FK_telefone_contacto
        FOREIGN KEY (id_contacto)
        REFERENCES manager.contacto (id_contacto)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ============================================
-- ÍNDICES para melhorar pesquisas
-- ============================================
CREATE INDEX IDX_contacto_nome
    ON manager.contacto (nome_completo);

CREATE INDEX IDX_contacto_utilizador
    ON manager.contacto (id_utilizador);

CREATE INDEX IDX_telefone_contacto
    ON manager.telefone (id_contacto);