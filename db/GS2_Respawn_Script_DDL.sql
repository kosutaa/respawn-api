-- =====================================================================
-- GLOBAL SOLUTION 2026.2 - CITIZENTECH (DATABASE DESIGN)
-- EQUIPE: SysCore
-- INTEGRANTES:
--   Guilherme Moura Gama - RM: 562162
--   Guilherme Ruiz Costa - RM: 563236
--   Julio Cesar Augusto Vieira - RM: 563366
-- =====================================================================

-- Limpeza previa das tabelas (OPCIONAL)
-- Descomente as linhas abaixo apenas se as tabelas ja existirem no seu
-- usuario. Em um banco limpo, os DROPs gerariam o erro ORA-00942.
-- DROP TABLE t_sc_resposta_aluno CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_missao_diaria CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_exercicio CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_topico CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_aluno CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_turma CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_professor CASCADE CONSTRAINTS;
-- DROP TABLE t_sc_escola CASCADE CONSTRAINTS;

-- 1. Tabela de Escolas Publicas
CREATE TABLE t_sc_escola (
    id_escola       NUMBER(10) NOT NULL,
    nm_escola       VARCHAR2(100) NOT NULL,
    ds_bairro       VARCHAR2(60) NOT NULL,
    ds_zona         VARCHAR2(30) NOT NULL,
    nr_inep         VARCHAR2(8) NOT NULL
);

ALTER TABLE t_sc_escola ADD CONSTRAINT pk_sc_escola PRIMARY KEY (id_escola);
ALTER TABLE t_sc_escola ADD CONSTRAINT uk_sc_escola_inep UNIQUE (nr_inep);

-- 2. Tabela de Professores
CREATE TABLE t_sc_professor (
    id_professor    NUMBER(10) NOT NULL,
    id_escola       NUMBER(10) NOT NULL,
    nm_professor    VARCHAR2(100) NOT NULL,
    ds_email        VARCHAR2(100) NOT NULL,
    ds_disciplina   VARCHAR2(30) NOT NULL
);

ALTER TABLE t_sc_professor ADD CONSTRAINT pk_sc_professor PRIMARY KEY (id_professor);
ALTER TABLE t_sc_professor ADD CONSTRAINT uk_sc_prof_email UNIQUE (ds_email);
ALTER TABLE t_sc_professor ADD CONSTRAINT ck_sc_prof_disc CHECK (ds_disciplina IN ('MATEMATICA', 'PORTUGUES'));

-- 3. Tabela de Turmas
CREATE TABLE t_sc_turma (
    id_turma        NUMBER(10) NOT NULL,
    id_escola       NUMBER(10) NOT NULL,
    id_professor    NUMBER(10) NOT NULL,
    nm_turma        VARCHAR2(30) NOT NULL,
    ds_serie        VARCHAR2(30) NOT NULL,
    ds_turno        VARCHAR2(20) NOT NULL,
    nr_ano_letivo   NUMBER(4) NOT NULL
);

ALTER TABLE t_sc_turma ADD CONSTRAINT pk_sc_turma PRIMARY KEY (id_turma);
ALTER TABLE t_sc_turma ADD CONSTRAINT ck_sc_turma_turno CHECK (ds_turno IN ('MANHA', 'TARDE', 'NOITE'));

-- 4. Tabela de Alunos
CREATE TABLE t_sc_aluno (
    id_aluno            NUMBER(10) NOT NULL,
    id_turma            NUMBER(10) NOT NULL,
    nm_aluno            VARCHAR2(100) NOT NULL,
    nr_idade            NUMBER(2) NOT NULL,
    qt_tempo_diario_min NUMBER(3) DEFAULT 15 NOT NULL,
    nr_xp_total         NUMBER(8) DEFAULT 0 NOT NULL,
    nr_nivel            NUMBER(4) DEFAULT 1 NOT NULL,
    nr_streak_dias      NUMBER(4) DEFAULT 0 NOT NULL,
    dt_ultimo_acesso    DATE
);

ALTER TABLE t_sc_aluno ADD CONSTRAINT pk_sc_aluno PRIMARY KEY (id_aluno);
ALTER TABLE t_sc_aluno ADD CONSTRAINT ck_sc_aluno_tempo CHECK (qt_tempo_diario_min > 0);
ALTER TABLE t_sc_aluno ADD CONSTRAINT ck_sc_aluno_xp CHECK (nr_xp_total >= 0);

-- 5. Tabela de Topicos Curriculares (com Auto-Relacionamento de Pre-Requisito)
CREATE TABLE t_sc_topico (
    id_topico           NUMBER(10) NOT NULL,
    id_topico_prereq    NUMBER(10),
    nm_topico           VARCHAR2(80) NOT NULL,
    ds_disciplina       VARCHAR2(30) NOT NULL,
    nr_peso_base        NUMBER(2) NOT NULL
);

ALTER TABLE t_sc_topico ADD CONSTRAINT pk_sc_topico PRIMARY KEY (id_topico);
ALTER TABLE t_sc_topico ADD CONSTRAINT ck_sc_topico_disc CHECK (ds_disciplina IN ('MATEMATICA', 'PORTUGUES'));
ALTER TABLE t_sc_topico ADD CONSTRAINT ck_sc_topico_peso CHECK (nr_peso_base BETWEEN 1 AND 10);

-- 6. Tabela de Exercicios (Microlearning)
CREATE TABLE t_sc_exercicio (
    id_exercicio        NUMBER(10) NOT NULL,
    id_topico           NUMBER(10) NOT NULL,
    ds_titulo           VARCHAR2(100) NOT NULL,
    ds_enunciado        VARCHAR2(400) NOT NULL,
    nr_tempo_estimado   NUMBER(2) NOT NULL,
    nr_xp_recompensa    NUMBER(4) NOT NULL,
    ds_dificuldade      VARCHAR2(15) NOT NULL
);

ALTER TABLE t_sc_exercicio ADD CONSTRAINT pk_sc_exercicio PRIMARY KEY (id_exercicio);
ALTER TABLE t_sc_exercicio ADD CONSTRAINT ck_sc_exerc_dif CHECK (ds_dificuldade IN ('BASICO', 'INTERMEDIARIO', 'AVANCADO'));
ALTER TABLE t_sc_exercicio ADD CONSTRAINT ck_sc_exerc_tempo CHECK (nr_tempo_estimado > 0);

-- 7. Tabela de Missoes Diarias
CREATE TABLE t_sc_missao_diaria (
    id_missao           NUMBER(10) NOT NULL,
    id_aluno            NUMBER(10) NOT NULL,
    dt_missao           DATE NOT NULL,
    nr_tempo_disp_min   NUMBER(3) NOT NULL,
    nr_xp_ganho         NUMBER(5) DEFAULT 0 NOT NULL,
    st_concluida        CHAR(1) DEFAULT 'N' NOT NULL
);

ALTER TABLE t_sc_missao_diaria ADD CONSTRAINT pk_sc_missao_diaria PRIMARY KEY (id_missao);
ALTER TABLE t_sc_missao_diaria ADD CONSTRAINT ck_sc_missao_status CHECK (st_concluida IN ('S', 'N'));

-- 8. Tabela de Respostas dos Alunos nos Exercicios
CREATE TABLE t_sc_resposta_aluno (
    id_resposta         NUMBER(10) NOT NULL,
    id_missao           NUMBER(10) NOT NULL,
    id_exercicio        NUMBER(10) NOT NULL,
    st_correta          CHAR(1) NOT NULL,
    nr_tempo_gasto_min  NUMBER(2) NOT NULL,
    dt_resposta         DATE NOT NULL
);

ALTER TABLE t_sc_resposta_aluno ADD CONSTRAINT pk_sc_resposta_aluno PRIMARY KEY (id_resposta);
ALTER TABLE t_sc_resposta_aluno ADD CONSTRAINT ck_sc_resp_correta CHECK (st_correta IN ('S', 'N'));

-- =====================================================================
-- CHAVES ESTRANGEIRAS (RELACIONAMENTOS)
-- =====================================================================

ALTER TABLE t_sc_professor
    ADD CONSTRAINT fk_sc_prof_escola FOREIGN KEY (id_escola)
    REFERENCES t_sc_escola (id_escola);

ALTER TABLE t_sc_turma
    ADD CONSTRAINT fk_sc_turma_escola FOREIGN KEY (id_escola)
    REFERENCES t_sc_escola (id_escola);

ALTER TABLE t_sc_turma
    ADD CONSTRAINT fk_sc_turma_prof FOREIGN KEY (id_professor)
    REFERENCES t_sc_professor (id_professor);

ALTER TABLE t_sc_aluno
    ADD CONSTRAINT fk_sc_aluno_turma FOREIGN KEY (id_turma)
    REFERENCES t_sc_turma (id_turma);

ALTER TABLE t_sc_topico
    ADD CONSTRAINT fk_sc_topico_prereq FOREIGN KEY (id_topico_prereq)
    REFERENCES t_sc_topico (id_topico);

ALTER TABLE t_sc_exercicio
    ADD CONSTRAINT fk_sc_exerc_topico FOREIGN KEY (id_topico)
    REFERENCES t_sc_topico (id_topico);

ALTER TABLE t_sc_missao_diaria
    ADD CONSTRAINT fk_sc_missao_aluno FOREIGN KEY (id_aluno)
    REFERENCES t_sc_aluno (id_aluno);

ALTER TABLE t_sc_resposta_aluno
    ADD CONSTRAINT fk_sc_resp_missao FOREIGN KEY (id_missao)
    REFERENCES t_sc_missao_diaria (id_missao);

ALTER TABLE t_sc_resposta_aluno
    ADD CONSTRAINT fk_sc_resp_exercicio FOREIGN KEY (id_exercicio)
    REFERENCES t_sc_exercicio (id_exercicio);