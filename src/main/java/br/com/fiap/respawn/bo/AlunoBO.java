package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.AlunoDAO;
import br.com.fiap.respawn.dao.MissaoDiariaDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.model.Aluno;
import br.com.fiap.respawn.model.MissaoDiaria;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negocio de alunos. */
@Service
public class AlunoBO {

    private final AlunoDAO alunoDAO;
    private final MissaoDiariaDAO missaoDAO;
    private final TurmaBO turmaBO;

    public AlunoBO(AlunoDAO alunoDAO, MissaoDiariaDAO missaoDAO, TurmaBO turmaBO) {
        this.alunoDAO = alunoDAO;
        this.missaoDAO = missaoDAO;
        this.turmaBO = turmaBO;
    }

    public List<Aluno> listar() {
        return alunoDAO.listar();
    }

    public Aluno buscar(Long id) {
        return alunoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", id));
    }

    /** Todo aluno novo comeca com 0 XP, nivel 1 e sem sequencia de dias. */
    public Aluno cadastrar(Aluno aluno) {
        turmaBO.buscar(aluno.getIdTurma());
        aluno.setId(null);
        aluno.setXpTotal(0);
        aluno.setNivel(1);
        aluno.setStreakDias(0);
        aluno.setUltimoAcesso(null);
        return alunoDAO.inserir(aluno);
    }

    /**
     * Atualiza os dados cadastrais. XP, nivel, sequencia e ultimo acesso sao
     * mantidos, pois so mudam pela gamificacao (respostas nas missoes).
     */
    public Aluno atualizar(Long id, Aluno dados) {
        Aluno atual = buscar(id);
        turmaBO.buscar(dados.getIdTurma());
        atual.setIdTurma(dados.getIdTurma());
        atual.setNome(dados.getNome());
        atual.setIdade(dados.getIdade());
        atual.setTempoDiarioMin(dados.getTempoDiarioMin());
        alunoDAO.atualizar(atual);
        return atual;
    }

    public void excluir(Long id) {
        buscar(id);
        alunoDAO.excluir(id);
    }

    public List<MissaoDiaria> missoes(Long idAluno) {
        buscar(idAluno);
        return missaoDAO.listarPorAluno(idAluno);
    }
}
