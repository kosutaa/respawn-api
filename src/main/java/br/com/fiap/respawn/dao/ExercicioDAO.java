package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.Exercicio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso a dados da tabela T_SC_EXERCICIO. */
@Repository
public class ExercicioDAO {

    private static final String COLUNAS = "id_exercicio, id_topico, ds_titulo, ds_enunciado, "
            + "nr_tempo_estimado, nr_xp_recompensa, ds_dificuldade";

    private static final RowMapper<Exercicio> MAPPER = (rs, i) -> new Exercicio(
            rs.getLong("id_exercicio"),
            rs.getLong("id_topico"),
            rs.getString("ds_titulo"),
            rs.getString("ds_enunciado"),
            rs.getInt("nr_tempo_estimado"),
            rs.getInt("nr_xp_recompensa"),
            rs.getString("ds_dificuldade"));

    private final JdbcTemplate jdbc;

    public ExercicioDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Exercicio> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_exercicio ORDER BY id_exercicio", MAPPER);
    }

    public List<Exercicio> listarPorTopico(Long idTopico) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_exercicio WHERE id_topico = ? ORDER BY nr_tempo_estimado",
                MAPPER, idTopico);
    }

    public Optional<Exercicio> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_exercicio WHERE id_exercicio = ?", MAPPER, id)
                .stream().findFirst();
    }

    public Exercicio inserir(Exercicio e) {
        Long id = jdbc.queryForObject("SELECT seq_sc_exercicio.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_exercicio (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, e.getIdTopico(), e.getTitulo(), e.getEnunciado(), e.getTempoEstimadoMin(),
                e.getXpRecompensa(), e.getDificuldade());
        e.setId(id);
        return e;
    }

    public void atualizar(Exercicio e) {
        jdbc.update("UPDATE t_sc_exercicio SET id_topico = ?, ds_titulo = ?, ds_enunciado = ?, nr_tempo_estimado = ?, "
                        + "nr_xp_recompensa = ?, ds_dificuldade = ? WHERE id_exercicio = ?",
                e.getIdTopico(), e.getTitulo(), e.getEnunciado(), e.getTempoEstimadoMin(),
                e.getXpRecompensa(), e.getDificuldade(), e.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_exercicio WHERE id_exercicio = ?", id);
    }
}
