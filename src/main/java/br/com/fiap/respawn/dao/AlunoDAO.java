package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.Aluno;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AlunoDAO {

    private static final String COLUNAS = "id_aluno, id_turma, nm_aluno, nr_idade, qt_tempo_diario_min, "
            + "nr_xp_total, nr_nivel, nr_streak_dias, dt_ultimo_acesso";

    private static final RowMapper<Aluno> MAPPER = (rs, i) -> new Aluno(
            rs.getLong("id_aluno"),
            rs.getLong("id_turma"),
            rs.getString("nm_aluno"),
            rs.getInt("nr_idade"),
            rs.getInt("qt_tempo_diario_min"),
            rs.getInt("nr_xp_total"),
            rs.getInt("nr_nivel"),
            rs.getInt("nr_streak_dias"),
            ConversorJdbc.data(rs, "dt_ultimo_acesso"));

    private final JdbcTemplate jdbc;

    public AlunoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Aluno> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_aluno ORDER BY id_aluno", MAPPER);
    }

    public List<Aluno> listarPorTurma(Long idTurma) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_aluno WHERE id_turma = ? ORDER BY nr_xp_total DESC",
                MAPPER, idTurma);
    }

    public List<Aluno> listarInativos(Long idTurma, LocalDate acessoAntesDe) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_aluno "
                        + "WHERE id_turma = ? AND (nr_streak_dias = 0 OR dt_ultimo_acesso IS NULL OR dt_ultimo_acesso < ?) "
                        + "ORDER BY dt_ultimo_acesso NULLS FIRST",
                MAPPER, idTurma, ConversorJdbc.data(acessoAntesDe));
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_aluno WHERE id_aluno = ?", MAPPER, id)
                .stream().findFirst();
    }

    public Aluno inserir(Aluno a) {
        Long id = jdbc.queryForObject("SELECT seq_sc_aluno.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_aluno (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, a.getIdTurma(), a.getNome(), a.getIdade(), a.getTempoDiarioMin(),
                a.getXpTotal(), a.getNivel(), a.getStreakDias(), ConversorJdbc.data(a.getUltimoAcesso()));
        a.setId(id);
        return a;
    }

    public void atualizar(Aluno a) {
        jdbc.update("UPDATE t_sc_aluno SET id_turma = ?, nm_aluno = ?, nr_idade = ?, qt_tempo_diario_min = ?, "
                        + "nr_xp_total = ?, nr_nivel = ?, nr_streak_dias = ?, dt_ultimo_acesso = ? WHERE id_aluno = ?",
                a.getIdTurma(), a.getNome(), a.getIdade(), a.getTempoDiarioMin(), a.getXpTotal(),
                a.getNivel(), a.getStreakDias(), ConversorJdbc.data(a.getUltimoAcesso()), a.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_aluno WHERE id_aluno = ?", id);
    }
}
