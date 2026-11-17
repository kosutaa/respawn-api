package br.com.fiap.respawn.dao;

import br.com.fiap.respawn.model.MissaoDiaria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acesso a dados da tabela T_SC_MISSAO_DIARIA. */
@Repository
public class MissaoDiariaDAO {

    private static final String COLUNAS =
            "id_missao, id_aluno, dt_missao, nr_tempo_disp_min, nr_xp_ganho, st_concluida";

    private static final RowMapper<MissaoDiaria> MAPPER = (rs, i) -> new MissaoDiaria(
            rs.getLong("id_missao"),
            rs.getLong("id_aluno"),
            ConversorJdbc.data(rs, "dt_missao"),
            rs.getInt("nr_tempo_disp_min"),
            rs.getInt("nr_xp_ganho"),
            ConversorJdbc.simNao(rs, "st_concluida"));

    private final JdbcTemplate jdbc;

    public MissaoDiariaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MissaoDiaria> listar() {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_missao_diaria ORDER BY id_missao", MAPPER);
    }

    public List<MissaoDiaria> listarPorAluno(Long idAluno) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_missao_diaria WHERE id_aluno = ? ORDER BY dt_missao DESC",
                MAPPER, idAluno);
    }

    public Optional<MissaoDiaria> buscarPorId(Long id) {
        return jdbc.query("SELECT " + COLUNAS + " FROM t_sc_missao_diaria WHERE id_missao = ?", MAPPER, id)
                .stream().findFirst();
    }

    public MissaoDiaria inserir(MissaoDiaria m) {
        Long id = jdbc.queryForObject("SELECT seq_sc_missao.NEXTVAL FROM dual", Long.class);
        jdbc.update("INSERT INTO t_sc_missao_diaria (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?)",
                id, m.getIdAluno(), ConversorJdbc.data(m.getData()), m.getTempoDisponivelMin(),
                m.getXpGanho(), ConversorJdbc.simNao(m.getConcluida()));
        m.setId(id);
        return m;
    }

    public void atualizar(MissaoDiaria m) {
        jdbc.update("UPDATE t_sc_missao_diaria SET id_aluno = ?, dt_missao = ?, nr_tempo_disp_min = ?, "
                        + "nr_xp_ganho = ?, st_concluida = ? WHERE id_missao = ?",
                m.getIdAluno(), ConversorJdbc.data(m.getData()), m.getTempoDisponivelMin(),
                m.getXpGanho(), ConversorJdbc.simNao(m.getConcluida()), m.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM t_sc_missao_diaria WHERE id_missao = ?", id);
    }
}
