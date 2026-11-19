package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.ExercicioDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.model.Exercicio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExercicioBO {

    private final ExercicioDAO exercicioDAO;
    private final TopicoBO topicoBO;

    public ExercicioBO(ExercicioDAO exercicioDAO, TopicoBO topicoBO) {
        this.exercicioDAO = exercicioDAO;
        this.topicoBO = topicoBO;
    }

    public List<Exercicio> listar() {
        return exercicioDAO.listar();
    }

    public Exercicio buscar(Long id) {
        return exercicioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercicio", id));
    }

    public Exercicio cadastrar(Exercicio exercicio) {
        topicoBO.buscar(exercicio.getIdTopico());
        exercicio.setId(null);
        return exercicioDAO.inserir(exercicio);
    }

    public Exercicio atualizar(Long id, Exercicio exercicio) {
        buscar(id);
        topicoBO.buscar(exercicio.getIdTopico());
        exercicio.setId(id);
        exercicioDAO.atualizar(exercicio);
        return exercicio;
    }

    public void excluir(Long id) {
        buscar(id);
        exercicioDAO.excluir(id);
    }
}
