package br.com.fiap.respawn.bo;

import br.com.fiap.respawn.dao.AlunoDAO;
import br.com.fiap.respawn.dao.MissaoDiariaDAO;
import br.com.fiap.respawn.dao.RespostaAlunoDAO;
import br.com.fiap.respawn.exception.RecursoNaoEncontradoException;
import br.com.fiap.respawn.exception.RegraDeNegocioException;
import br.com.fiap.respawn.model.Aluno;
import br.com.fiap.respawn.model.Exercicio;
import br.com.fiap.respawn.model.MissaoDiaria;
import br.com.fiap.respawn.model.RespostaAluno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RespostaAlunoBO {

    private final RespostaAlunoDAO respostaDAO;
    private final MissaoDiariaDAO missaoDAO;
    private final AlunoDAO alunoDAO;
    private final MissaoDiariaBO missaoBO;
    private final ExercicioBO exercicioBO;
    private final AlunoBO alunoBO;

    public RespostaAlunoBO(RespostaAlunoDAO respostaDAO, MissaoDiariaDAO missaoDAO, AlunoDAO alunoDAO,
                           MissaoDiariaBO missaoBO, ExercicioBO exercicioBO, AlunoBO alunoBO) {
        this.respostaDAO = respostaDAO;
        this.missaoDAO = missaoDAO;
        this.alunoDAO = alunoDAO;
        this.missaoBO = missaoBO;
        this.exercicioBO = exercicioBO;
        this.alunoBO = alunoBO;
    }

    public List<RespostaAluno> listar() {
        return respostaDAO.listar();
    }

    public List<RespostaAluno> listarPorMissao(Long idMissao) {
        missaoBO.buscar(idMissao);
        return respostaDAO.listarPorMissao(idMissao);
    }

    public RespostaAluno buscar(Long id) {
        return respostaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Resposta", id));
    }

    // XP so entra se acertou; a sequencia de dias conta mesmo se errou
    @Transactional
    public RespostaAluno registrar(Long idMissao, RespostaAluno resposta) {
        MissaoDiaria missao = missaoBO.buscar(idMissao);
        Exercicio exercicio = exercicioBO.buscar(resposta.getIdExercicio());
        Aluno aluno = alunoBO.buscar(missao.getIdAluno());

        if (Boolean.TRUE.equals(missao.getConcluida())) {
            throw new RegraDeNegocioException("A missao ja foi concluida e nao aceita novas respostas");
        }
        if (respostaDAO.exercicioJaRespondido(idMissao, exercicio.getId())) {
            throw new RegraDeNegocioException("O exercicio " + exercicio.getId() + " ja foi respondido nesta missao");
        }
        int tempoTotal = respostaDAO.tempoGastoNaMissao(idMissao) + resposta.getTempoGastoMin();
        if (tempoTotal > missao.getTempoDisponivelMin()) {
            throw new RegraDeNegocioException("Essa resposta ultrapassa o tempo disponivel da missao ("
                    + tempoTotal + " de " + missao.getTempoDisponivelMin() + " minutos)");
        }

        resposta.setId(null);
        resposta.setIdMissao(idMissao);
        resposta.setData(Datas.hoje());
        respostaDAO.inserir(resposta);

        if (Boolean.TRUE.equals(resposta.getCorreta())) {
            missao.setXpGanho(missao.getXpGanho() + exercicio.getXpRecompensa());
            missaoDAO.atualizar(missao);
            aluno.adicionarXp(exercicio.getXpRecompensa());
        }
        aluno.registrarEstudo(resposta.getData());
        alunoDAO.atualizar(aluno);

        return resposta;
    }

    @Transactional
    public void excluir(Long id) {
        RespostaAluno resposta = buscar(id);
        MissaoDiaria missao = missaoBO.buscar(resposta.getIdMissao());
        if (Boolean.TRUE.equals(missao.getConcluida())) {
            throw new RegraDeNegocioException("Nao e possivel excluir respostas de uma missao concluida");
        }
        if (Boolean.TRUE.equals(resposta.getCorreta())) {
            Exercicio exercicio = exercicioBO.buscar(resposta.getIdExercicio());
            missao.setXpGanho(Math.max(0, missao.getXpGanho() - exercicio.getXpRecompensa()));
            missaoDAO.atualizar(missao);
            Aluno aluno = alunoBO.buscar(missao.getIdAluno());
            aluno.adicionarXp(-exercicio.getXpRecompensa());
            alunoDAO.atualizar(aluno);
        }
        respostaDAO.excluir(id);
    }
}
