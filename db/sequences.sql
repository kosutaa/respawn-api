-- =====================================================================
-- RESPAWN - API JAVA (DOMAIN DRIVEN DESIGN)
-- Sequences para gerar os IDs dos registros criados pela API (POST).
-- Rodar DEPOIS dos scripts DDL e DML da pasta /database.
-- Comecam em 100 para nao colidir com os IDs fixos do DML.
-- (Nao faz parte da entrega de Database Design.)
-- =====================================================================

CREATE SEQUENCE seq_sc_escola    START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_professor START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_turma     START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_aluno     START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_topico    START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_exercicio START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_missao    START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_sc_resposta  START WITH 100 INCREMENT BY 1 NOCACHE;
