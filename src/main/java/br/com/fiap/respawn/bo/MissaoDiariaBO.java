package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.MissaoDiariaDAO;
import br.com.fiap.respawn.dao.RespostaAlunoDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.exception.RegraDeNegocioException;
import br.com.fiap.respawn.model.MissaoDiaria;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negocio das missoes diarias. */
@Service
public class MissaoDiariaBO {

    private final MissaoDiariaDAO missaoDAO;
    private final RespostaAlunoDAO respostaDAO;
    private final AlunoBO alunoBO;

    public MissaoDiariaBO(MissaoDiariaDAO missaoDAO, RespostaAlunoDAO respostaDAO, AlunoBO alunoBO) {
        this.missaoDAO = missaoDAO;
        this.respostaDAO = respostaDAO;
        this.alunoBO = alunoBO;
    }

    public List<MissaoDiaria> listar() {
        return missaoDAO.listar();
    }

    public MissaoDiaria buscar(Long id) {
        return missaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Missao", id));
    }

    /** Toda missao nova comeca aberta e com 0 XP; sem data, vale para hoje. */
    public MissaoDiaria cadastrar(MissaoDiaria missao) {
        alunoBO.buscar(missao.getIdAluno());
        missao.setId(null);
        missao.setXpGanho(0);
        missao.setConcluida(false);
        if (missao.getData() == null) {
            missao.setData(Datas.hoje());
        }
        return missaoDAO.inserir(missao);
    }

    /**
     * Permite ajustar data, tempo disponivel e status. O aluno e o XP ganho
     * nao mudam por aqui. O tempo nao pode ficar menor do que o ja gasto.
     */
    public MissaoDiaria atualizar(Long id, MissaoDiaria dados) {
        MissaoDiaria atual = buscar(id);
        int tempoGasto = respostaDAO.tempoGastoNaMissao(id);
        if (dados.getTempoDisponivelMin() < tempoGasto) {
            throw new RegraDeNegocioException("O tempo disponivel nao pode ser menor que os "
                    + tempoGasto + " minutos ja gastos nesta missao");
        }
        if (dados.getData() != null) {
            atual.setData(dados.getData());
        }
        atual.setTempoDisponivelMin(dados.getTempoDisponivelMin());
        if (dados.getConcluida() != null) {
            atual.setConcluida(dados.getConcluida());
        }
        missaoDAO.atualizar(atual);
        return atual;
    }

    public MissaoDiaria concluir(Long id) {
        MissaoDiaria missao = buscar(id);
        if (Boolean.TRUE.equals(missao.getConcluida())) {
            throw new RegraDeNegocioException("A missao ja esta concluida");
        }
        if (respostaDAO.listarPorMissao(id).isEmpty()) {
            throw new RegraDeNegocioException("Nao e possivel concluir uma missao sem nenhuma resposta");
        }
        missao.setConcluida(true);
        missaoDAO.atualizar(missao);
        return missao;
    }

    public void excluir(Long id) {
        buscar(id);
        if (!respostaDAO.listarPorMissao(id).isEmpty()) {
            throw new RegraDeNegocioException("A missao possui respostas registradas. Exclua as respostas antes");
        }
        missaoDAO.excluir(id);
    }
}
