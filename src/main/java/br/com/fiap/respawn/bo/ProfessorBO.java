package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.ProfessorDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.model.Professor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Regras de negocio de professores. */
@Service
public class ProfessorBO {

    private final ProfessorDAO professorDAO;
    private final EscolaBO escolaBO;

    public ProfessorBO(ProfessorDAO professorDAO, EscolaBO escolaBO) {
        this.professorDAO = professorDAO;
        this.escolaBO = escolaBO;
    }

    public List<Professor> listar() {
        return professorDAO.listar();
    }

    public Professor buscar(Long id) {
        return professorDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor", id));
    }

    public Professor cadastrar(Professor professor) {
        escolaBO.buscar(professor.getIdEscola());
        professor.setId(null);
        professor.setEmail(professor.getEmail().trim().toLowerCase());
        return professorDAO.inserir(professor);
    }

    public Professor atualizar(Long id, Professor professor) {
        buscar(id);
        escolaBO.buscar(professor.getIdEscola());
        professor.setId(id);
        professor.setEmail(professor.getEmail().trim().toLowerCase());
        professorDAO.atualizar(professor);
        return professor;
    }

    public void excluir(Long id) {
        buscar(id);
        professorDAO.excluir(id);
    }
}
