package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.Professor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso a dados da tabela T_SC_PROFESSOR. */
@Repository
public class ProfessorDAO {

    private static final String COLUNAS = "id_professor, id_escola, nm_professor, ds_email, ds_disciplina";

    private static final RowMapper<Professor> MAPPER = (rs, i) -> new Professor(
            rs.getLong("id_professor"),
            rs.getLong("id_escola"),
            rs.getString("nm_professor"),
            rs.getString("ds_email"),
            rs.getString("ds_disciplina"));

    private final JdbcTemplate jdbc;

    public ProfessorDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Professor> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_professor ORDER BY id_professor", MAPPER);
    }

    public Optional<Professor> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_professor WHERE id_professor = ?", MAPPER, id)
                .stream().findFirst();
    }

    public Professor inserir(Professor p) {
        Long id = jdbc.queryForObject("SELECT seq_sc_professor.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_professor (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?)",
                id, p.getIdEscola(), p.getNome(), p.getEmail(), p.getDisciplina());
        p.setId(id);
        return p;
    }

    public void atualizar(Professor p) {
        jdbc.update("UPDATE t_sc_professor SET id_escola = ?, nm_professor = ?, ds_email = ?, ds_disciplina = ? "
                        + "WHERE id_professor = ?",
                p.getIdEscola(), p.getNome(), p.getEmail(), p.getDisciplina(), p.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_professor WHERE id_professor = ?", id);
    }
}
