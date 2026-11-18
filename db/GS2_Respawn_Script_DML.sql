-- =====================================================================
-- GLOBAL SOLUTION 2026.2 - CITIZENTECH (DATABASE DESIGN - DML)
-- EQUIPE: SysCore
-- =====================================================================

-- 1. Inserindo 5 Escolas Publicas
INSERT INTO t_sc_escola VALUES (1, 'E.E. Prof. Carlos de Moraes', 'Itaquera', 'Zona Leste', '35001122');
INSERT INTO t_sc_escola VALUES (2, 'E.E. Dom Paulo Evaristo Arns', 'Sao Mateus', 'Zona Leste', '35001123');
INSERT INTO t_sc_escola VALUES (3, 'E.E. Cora Coralina', 'Guaianases', 'Zona Leste', '35001124');
INSERT INTO t_sc_escola VALUES (4, 'E.E. Machado de Assis', 'Penha', 'Zona Leste', '35001125');
INSERT INTO t_sc_escola VALUES (5, 'E.E. Carolina Maria de Jesus', 'Cidade Tiradentes', 'Zona Leste', '35001126');

-- 2. Inserindo 5 Professores
INSERT INTO t_sc_professor VALUES (1, 1, 'Helena Souza', 'helena.souza@escola.example', 'MATEMATICA');
INSERT INTO t_sc_professor VALUES (2, 1, 'Marcos Oliveira', 'marcos.oliveira@escola.example', 'PORTUGUES');
INSERT INTO t_sc_professor VALUES (3, 2, 'Claudia Ferreira', 'claudia.ferreira@escola.example', 'MATEMATICA');
INSERT INTO t_sc_professor VALUES (4, 3, 'Roberto Santos', 'roberto.santos@escola.example', 'PORTUGUES');
INSERT INTO t_sc_professor VALUES (5, 4, 'Patricia Lima', 'patricia.lima@escola.example', 'MATEMATICA');

-- 3. Inserindo 5 Turmas
INSERT INTO t_sc_turma VALUES (1, 1, 1, '1A-EM', '1o Ano Ensino Medio', 'MANHA', 2026);
INSERT INTO t_sc_turma VALUES (2, 1, 2, '9B-EF', '9o Ano Fundamental', 'TARDE', 2026);
INSERT INTO t_sc_turma VALUES (3, 2, 3, '1B-EM', '1o Ano Ensino Medio', 'MANHA', 2026);
INSERT INTO t_sc_turma VALUES (4, 3, 4, '9A-EF', '9o Ano Fundamental', 'TARDE', 2026);
INSERT INTO t_sc_turma VALUES (5, 4, 5, '2A-EM', '2o Ano Ensino Medio', 'NOITE', 2026);

-- 4. Inserindo 6 Alunos (incluindo a personagem Ana e alunos inativos para o painel)
INSERT INTO t_sc_aluno VALUES (1, 1, 'Ana Beatriz Silva', 15, 15, 450, 4, 7, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_aluno VALUES (2, 1, 'Lucas Gabriel Costa', 15, 20, 320, 3, 4, TO_DATE('2026-11-15', 'YYYY-MM-DD'));
INSERT INTO t_sc_aluno VALUES (3, 1, 'Matheus Henrique Rocha', 16, 15, 120, 1, 0, TO_DATE('2026-10-28', 'YYYY-MM-DD'));
INSERT INTO t_sc_aluno VALUES (4, 2, 'Beatriz Almeida', 14, 15, 580, 5, 12, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_aluno VALUES (5, 2, 'Pedro Henrique Dias', 14, 10, 90, 1, 0, TO_DATE('2026-10-20', 'YYYY-MM-DD'));
INSERT INTO t_sc_aluno VALUES (6, 3, 'Camila Vitoria Alves', 15, 15, 250, 2, 2, TO_DATE('2026-11-14', 'YYYY-MM-DD'));

-- 5. Inserindo 6 Topicos com arvore de pre-requisitos
INSERT INTO t_sc_topico VALUES (1, NULL, 'Operacoes Basicas e Divisao', 'MATEMATICA', 10);
INSERT INTO t_sc_topico VALUES (2, 1, 'Fracoes e Proporcoes', 'MATEMATICA', 9);
INSERT INTO t_sc_topico VALUES (3, 2, 'Porcentagem Aplicada', 'MATEMATICA', 8);
INSERT INTO t_sc_topico VALUES (4, 2, 'Regra de Tres Simples', 'MATEMATICA', 8);
INSERT INTO t_sc_topico VALUES (5, NULL, 'Leitura e Localizacao de Informacoes', 'PORTUGUES', 10);
INSERT INTO t_sc_topico VALUES (6, 5, 'Interpretacao de Texto e Inferencia', 'PORTUGUES', 9);

-- 6. Inserindo 6 Exercicios Rapidos (2 a 7 minutos)
INSERT INTO t_sc_exercicio VALUES (1, 1, 'Divisao no Dia a Dia', 'Calcule a divisao exata de 144 itens em 12 caixas iguais.', 4, 30, 'BASICO');
INSERT INTO t_sc_exercicio VALUES (2, 2, 'Soma de Fracoes no Onibus', 'Se 1/4 do onibus desce na estacao e mais 2/4 na seguinte, qual fracao desceu?', 5, 50, 'BASICO');
INSERT INTO t_sc_exercicio VALUES (3, 2, 'Equivalencia de Fracoes', 'Qual fracao abaixo equivale exatamente a metade (1/2) de uma receita?', 5, 45, 'INTERMEDIARIO');
INSERT INTO t_sc_exercicio VALUES (4, 3, 'Desconto de 25% na Feira', 'Transforme 1/4 em porcentagem para achar o desconto de uma compra de R$ 80.', 6, 60, 'INTERMEDIARIO');
INSERT INTO t_sc_exercicio VALUES (5, 5, 'Ideia Central da Noticia', 'Leia o paragrafo curto e identifique a tese principal do autor.', 5, 40, 'BASICO');
INSERT INTO t_sc_exercicio VALUES (6, 6, 'Ironia e Sentido Figurado', 'Identifique a intencao implicita na fala do personagem da tirinha.', 6, 55, 'INTERMEDIARIO');

-- 7. Inserindo 5 Missoes Diarias
-- nr_xp_ganho = soma do XP dos exercicios respondidos corretamente na missao
INSERT INTO t_sc_missao_diaria VALUES (1, 1, TO_DATE('2026-11-15', 'YYYY-MM-DD'), 15, 0, 'S');
INSERT INTO t_sc_missao_diaria VALUES (2, 1, TO_DATE('2026-11-16', 'YYYY-MM-DD'), 15, 125, 'S');
INSERT INTO t_sc_missao_diaria VALUES (3, 2, TO_DATE('2026-11-15', 'YYYY-MM-DD'), 20, 0, 'S');
INSERT INTO t_sc_missao_diaria VALUES (4, 4, TO_DATE('2026-11-16', 'YYYY-MM-DD'), 15, 110, 'S');
INSERT INTO t_sc_missao_diaria VALUES (5, 6, TO_DATE('2026-11-14', 'YYYY-MM-DD'), 15, 40, 'N');

-- 8. Inserindo 9 Respostas de Exercicios
INSERT INTO t_sc_resposta_aluno VALUES (1, 1, 2, 'N', 5, TO_DATE('2026-11-15', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (2, 1, 4, 'N', 6, TO_DATE('2026-11-15', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (3, 2, 1, 'S', 4, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (4, 2, 2, 'S', 5, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (5, 2, 3, 'S', 5, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (6, 3, 4, 'N', 6, TO_DATE('2026-11-15', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (7, 4, 2, 'S', 5, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (8, 4, 4, 'S', 6, TO_DATE('2026-11-16', 'YYYY-MM-DD'));
INSERT INTO t_sc_resposta_aluno VALUES (9, 5, 5, 'S', 5, TO_DATE('2026-11-14', 'YYYY-MM-DD'));

COMMIT;