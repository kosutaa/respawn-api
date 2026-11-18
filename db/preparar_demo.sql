-- ============================================================================
-- Respawn - preparar o banco para a demonstracao (video pitch / apresentacao)
--
-- Volta a aluna Ana Beatriz Silva (id 1) ao estado da persona:
-- 450 XP, nivel 4, sequencia de 7 dias e ultimo acesso ONTEM.
-- Assim, ao responder a missao de hoje no video, a sequencia vai para 8 dias
-- e ela sobe para o nivel 5 ao completar a missao de 15 minutos (+125 XP).
--
-- Tambem apaga as missoes e respostas da Ana criadas pela aplicacao
-- (ids a partir de 100, gerados pelas sequences). Os dados do script DML
-- (ids 1 a 9) nao sao alterados.
--
-- Como usar: abrir no VS Code (extensao Oracle) conectado ao banco da FIAP
-- e executar o script inteiro (F5) antes de gravar.
-- ============================================================================

DELETE FROM t_sc_resposta_aluno
 WHERE id_missao IN (SELECT id_missao FROM t_sc_missao_diaria
                      WHERE id_aluno = 1 AND id_missao >= 100);

DELETE FROM t_sc_missao_diaria
 WHERE id_aluno = 1 AND id_missao >= 100;

UPDATE t_sc_aluno
   SET nr_xp_total      = 450,
       nr_nivel         = 4,
       nr_streak_dias   = 7,
       dt_ultimo_acesso = TRUNC(SYSDATE) - 1
 WHERE id_aluno = 1;

COMMIT;

-- Conferencia: deve mostrar Ana com 450 XP, nivel 4, sequencia 7 e acesso ontem
SELECT id_aluno, nm_aluno, nr_xp_total, nr_nivel, nr_streak_dias, dt_ultimo_acesso
  FROM t_sc_aluno
 WHERE id_aluno = 1;
