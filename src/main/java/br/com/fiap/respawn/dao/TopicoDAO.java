package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.Topico;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso a dados da tabela T_SC_TOPICO. */
@Repository
public class TopicoDAO {

    private static final String COLUNAS = "id_topico, id_topico_prereq, nm_topico, ds_disciplina, nr_peso_base";

    private static final RowMapper<Topico> MAPPER = (rs, i) -> new Topico(
            rs.getLong("id_topico"),
            ConversorJdbc.longOuNulo(rs, "id_topico_prereq"),
            rs.getString("nm_topico"),
            rs.getString("ds_disciplina"),
            rs.getInt("nr_peso_base"));

    private final JdbcTemplate jdbc;

    public TopicoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Topico> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_topico ORDER BY id_topico", MAPPER);
    }

    public Optional<Topico> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_topico WHERE id_topico = ?", MAPPER, id)
                .stream().findFirst();
    }

    public Topico inserir(Topico t) {
        Long id = jdbc.queryForObject("SELECT seq_sc_topico.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_topico (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?)",
                id, t.getIdTopicoPrereq(), t.getNome(), t.getDisciplina(), t.getPesoBase());
        t.setId(id);
        return t;
    }

    public void atualizar(Topico t) {
        jdbc.update("UPDATE t_sc_topico SET id_topico_prereq = ?, nm_topico = ?, ds_disciplina = ?, nr_peso_base = ? "
                        + "WHERE id_topico = ?",
                t.getIdTopicoPrereq(), t.getNome(), t.getDisciplina(), t.getPesoBase(), t.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_topico WHERE id_topico = ?", id);
    }
}
