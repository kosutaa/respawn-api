package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.RespostaAluno;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso a dados da tabela T_SC_RESPOSTA_ALUNO. */
@Repository
public class RespostaAlunoDAO {

    private static final String COLUNAS =
            "id_resposta, id_missao, id_exercicio, st_correta, nr_tempo_gasto_min, dt_resposta";

    private static final RowMapper<RespostaAluno> MAPPER = (rs, i) -> new RespostaAluno(
            rs.getLong("id_resposta"),
            rs.getLong("id_missao"),
            rs.getLong("id_exercicio"),
            ConversorJdbc.simNao(rs, "st_correta"),
            rs.getInt("nr_tempo_gasto_min"),
            ConversorJdbc.data(rs, "dt_resposta"));

    private final JdbcTemplate jdbc;

    public RespostaAlunoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<RespostaAluno> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_resposta_aluno ORDER BY id_resposta", MAPPER);
    }

    public List<RespostaAluno> listarPorMissao(Long idMissao) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_resposta_aluno WHERE id_missao = ? ORDER BY id_resposta",
                MAPPER, idMissao);
    }

    public Optional<RespostaAluno> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_resposta_aluno WHERE id_resposta = ?", MAPPER, id)
                .stream().findFirst();
    }

    /** Soma dos minutos ja gastos nas respostas de uma missao. */
    public int tempoGastoNaMissao(Long idMissao) {
        Integer total = jdbc.queryForObject(
                "SELECT NVL(SUM(nr_tempo_gasto_min), 0) FROM t_sc_resposta_aluno WHERE id_missao = ?",
                Integer.class, idMissao);
        return total == null ? 0 : total;
    }

    public boolean exercicioJaRespondido(Long idMissao, Long idExercicio) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM t_sc_resposta_aluno WHERE id_missao = ? AND id_exercicio = ?",
                Integer.class, idMissao, idExercicio);
        return total != null && total > 0;
    }

    public RespostaAluno inserir(RespostaAluno r) {
        Long id = jdbc.queryForObject("SELECT seq_sc_resposta.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_resposta_aluno (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?)",
                id, r.getIdMissao(), r.getIdExercicio(), ConversorJdbc.simNao(r.getCorreta()),
                r.getTempoGastoMin(), ConversorJdbc.data(r.getData()));
        r.setId(id);
        return r;
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_resposta_aluno WHERE id_resposta = ?", id);
    }
}
