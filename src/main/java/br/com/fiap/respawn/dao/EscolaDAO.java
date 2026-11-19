package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.Escola;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EscolaDAO {

    private static final String COLUNAS = "id_escola, nm_escola, ds_bairro, ds_zona, nr_inep";

    private static final RowMapper<Escola> MAPPER = (rs, i) -> new Escola(
            rs.getLong("id_escola"),
            rs.getString("nm_escola"),
            rs.getString("ds_bairro"),
            rs.getString("ds_zona"),
            rs.getString("nr_inep"));

    private final JdbcTemplate jdbc;

    public EscolaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Escola> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_escola ORDER BY id_escola", MAPPER);
    }

    public Optional<Escola> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_escola WHERE id_escola = ?", MAPPER, id)
                .stream().findFirst();
    }

    public boolean existe(Long id) {
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM t_sc_escola WHERE id_escola = ?", Integer.class, id);
        return total != null && total > 0;
    }

    public Escola inserir(Escola e) {
        Long id = jdbc.queryForObject("SELECT seq_sc_escola.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_escola (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?)",
                id, e.getNome(), e.getBairro(), e.getZona(), e.getInep());
        e.setId(id);
        return e;
    }

    public void atualizar(Escola e) {
        jdbc.update("UPDATE t_sc_escola SET nm_escola = ?, ds_bairro = ?, ds_zona = ?, nr_inep = ? WHERE id_escola = ?",
                e.getNome(), e.getBairro(), e.getZona(), e.getInep(), e.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_escola WHERE id_escola = ?", id);
    }
}
