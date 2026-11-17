package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.AlunoDAO;
import br.com.fiap.respawn.dao.TurmaDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.exception.RegraDeNegocioException;
import br.com.fiap.respawn.model.Aluno;
import br.com.fiap.respawn.model.ErroPorTopico;
import br.com.fiap.respawn.model.Professor;
import br.com.fiap.respawn.model.Turma;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negocio de turmas e do painel do professor. */
@Service
public class TurmaBO {

    private final TurmaDAO turmaDAO;
    private final AlunoDAO alunoDAO;
    private final EscolaBO escolaBO;
    private final ProfessorBO professorBO;

    public TurmaBO(TurmaDAO turmaDAO, AlunoDAO alunoDAO, EscolaBO escolaBO, ProfessorBO professorBO) {
        this.turmaDAO = turmaDAO;
        this.alunoDAO = alunoDAO;
        this.escolaBO = escolaBO;
        this.professorBO = professorBO;
    }

    public List<Turma> listar() {
        return turmaDAO.listar();
    }

    public Turma buscar(Long id) {
        return turmaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma", id));
    }

    public Turma cadastrar(Turma turma) {
        validar(turma);
        turma.setId(null);
        return turmaDAO.inserir(turma);
    }

    public Turma atualizar(Long id, Turma turma) {
        buscar(id);
        validar(turma);
        turma.setId(id);
        turmaDAO.atualizar(turma);
        return turma;
    }

    public void excluir(Long id) {
        buscar(id);
        turmaDAO.excluir(id);
    }

    public List<Aluno> alunos(Long idTurma) {
        buscar(idTurma);
        return alunoDAO.listarPorTurma(idTurma);
    }

    /** Painel do professor: topicos em que a turma mais erra. */
    public List<ErroPorTopico> errosPorTopico(Long idTurma) {
        buscar(idTurma);
        return turmaDAO.errosPorTopico(idTurma);
    }

    /** Busca ativa: alunos sem sequencia ativa ou sem acessar ha mais de N dias. */
    public List<Aluno> alunosInativos(Long idTurma, int dias) {
        if (dias < 1) {
            throw new RegraDeNegocioException("O numero de dias deve ser maior que zero");
        }
        buscar(idTurma);
        return alunoDAO.listarInativos(idTurma, Datas.hoje().minusDays(dias));
    }

    /** A escola e o professor precisam existir, e o professor precisa ser da mesma escola da turma. */
    private void validar(Turma turma) {
        escolaBO.buscar(turma.getIdEscola());
        Professor professor = professorBO.buscar(turma.getIdProfessor());
        if (!professor.getIdEscola().equals(turma.getIdEscola())) {
            throw new RegraDeNegocioException("O professor " + professor.getNome()
                    + " nao pertence a escola informada para a turma");
        }
    }
}
