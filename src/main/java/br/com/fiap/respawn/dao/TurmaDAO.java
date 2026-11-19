package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.ErroPorTopico;
import br.com.fiap.respawn.model.Turma;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TurmaDAO {

    private static final String COLUNAS =
            "id_turma, id_escola, id_professor, nm_turma, ds_serie, ds_turno, nr_ano_letivo";

    private static final RowMapper<Turma> MAPPER = (rs, i) -> new Turma(
            rs.getLong("id_turma"),
            rs.getLong("id_escola"),
            rs.getLong("id_professor"),
            rs.getString("nm_turma"),
            rs.getString("ds_serie"),
            rs.getString("ds_turno"),
            rs.getInt("nr_ano_letivo"));

    private final JdbcTemplate jdbc;

    public TurmaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Turma> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_turma ORDER BY id_turma", MAPPER);
    }

    public Optional<Turma> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_turma WHERE id_turma = ?", MAPPER, id)
                .stream().findFirst();
    }

    public Turma inserir(Turma t) {
        Long id = jdbc.queryForObject("SELECT seq_sc_turma.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_turma (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, t.getIdEscola(), t.getIdProfessor(), t.getNome(), t.getSerie(), t.getTurno(), t.getAnoLetivo());
        t.setId(id);
        return t;
    }

    public void atualizar(Turma t) {
        jdbc.update("UPDATE t_sc_turma SET id_escola = ?, id_professor = ?, nm_turma = ?, ds_serie = ?, "
                        + "ds_turno = ?, nr_ano_letivo = ? WHERE id_turma = ?",
                t.getIdEscola(), t.getIdProfessor(), t.getNome(), t.getSerie(), t.getTurno(),
                t.getAnoLetivo(), t.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_turma WHERE id_turma = ?", id);
    }

    public List<ErroPorTopico> errosPorTopico(Long idTurma) {
        String sql = """
                SELECT t.id_topico,
                       t.nm_topico,
                       t.ds_disciplina,
                       COUNT(r.id_resposta) AS total_respostas,
                       SUM(CASE WHEN r.st_correta = 'N' THEN 1 ELSE 0 END) AS total_erros
                  FROM t_sc_resposta_aluno r
                  INNER JOIN t_sc_missao_diaria m ON m.id_missao = r.id_missao
                  INNER JOIN t_sc_aluno a ON a.id_aluno = m.id_aluno
                  INNER JOIN t_sc_exercicio e ON e.id_exercicio = r.id_exercicio
                  INNER JOIN t_sc_topico t ON t.id_topico = e.id_topico
                 WHERE a.id_turma = ?
                 GROUP BY t.id_topico, t.nm_topico, t.ds_disciplina
                 ORDER BY total_erros DESC, t.nm_topico
                """;
        return jdbc.query(sql, (rs, i) -> new ErroPorTopico(
                rs.getLong("id_topico"),
                rs.getString("nm_topico"),
                rs.getString("ds_disciplina"),
                rs.getInt("total_respostas"),
                rs.getInt("total_erros")), idTurma);
    }
}
