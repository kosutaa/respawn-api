package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.ExercicioDAO;
import br.com.fiap.respawn.dao.TopicoDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.exception.RegraDeNegocioException;
import br.com.fiap.respawn.model.Exercicio;
import br.com.fiap.respawn.model.Topico;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Regras de negocio de topicos curriculares e da trilha de pre-requisitos. */
@Service
public class TopicoBO {

    private final TopicoDAO topicoDAO;
    private final ExercicioDAO exercicioDAO;

    public TopicoBO(TopicoDAO topicoDAO, ExercicioDAO exercicioDAO) {
        this.topicoDAO = topicoDAO;
        this.exercicioDAO = exercicioDAO;
    }

    public List<Topico> listar() {
        return topicoDAO.listar();
    }

    public Topico buscar(Long id) {
        return topicoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Topico", id));
    }

    public Topico cadastrar(Topico topico) {
        topico.setId(null);
        validarPreRequisito(topico);
        return topicoDAO.inserir(topico);
    }

    public Topico atualizar(Long id, Topico topico) {
        buscar(id);
        topico.setId(id);
        validarPreRequisito(topico);
        topicoDAO.atualizar(topico);
        return topico;
    }

    public void excluir(Long id) {
        buscar(id);
        topicoDAO.excluir(id);
    }

    public List<Exercicio> exercicios(Long idTopico) {
        buscar(idTopico);
        return exercicioDAO.listarPorTopico(idTopico);
    }

    /**
     * Trilha de estudo ate o topico: segue os pre-requisitos ate a base e
     * devolve na ordem em que devem ser estudados
     * (ex.: Operacoes Basicas -> Fracoes -> Porcentagem).
     */
    public List<Topico> trilha(Long idTopico) {
        List<Topico> trilha = new ArrayList<>();
        Topico atual = buscar(idTopico);
        trilha.add(atual);
        while (atual.getIdTopicoPrereq() != null) {
            atual = buscar(atual.getIdTopicoPrereq());
            trilha.add(atual);
        }
        Collections.reverse(trilha);
        return trilha;
    }

    /**
     * O pre-requisito (se informado) precisa existir, ser da mesma disciplina,
     * nao pode ser o proprio topico e nao pode criar um ciclo
     * (A exige B, que exige A), o que travaria a trilha.
     */
    private void validarPreRequisito(Topico topico) {
        Long idPrereq = topico.getIdTopicoPrereq();
        if (idPrereq == null) {
            return;
        }
        if (idPrereq.equals(topico.getId())) {
            throw new RegraDeNegocioException("Um topico nao pode ser pre-requisito de si mesmo");
        }
        Topico prereq = buscar(idPrereq);
        if (!prereq.getDisciplina().equals(topico.getDisciplina())) {
            throw new RegraDeNegocioException("O pre-requisito deve ser da mesma disciplina do topico");
        }
        Topico atual = prereq;
        while (atual.getIdTopicoPrereq() != null) {
            if (atual.getIdTopicoPrereq().equals(topico.getId())) {
                throw new RegraDeNegocioException("Esse pre-requisito criaria um ciclo na trilha de topicos");
            }
            atual = buscar(atual.getIdTopicoPrereq());
        }
    }
}
