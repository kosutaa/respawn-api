package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.RespostaAlunoBO;
import br.com.fiap.respawn.model.RespostaAluno;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consulta e exclusao de respostas. O cadastro e feito dentro da missao
 * (POST /missoes/{id}/respostas), porque toda resposta pertence a uma missao.
 */
@RestController
@RequestMapping("/respostas")
public class RespostaAlunoController {

    private final RespostaAlunoBO respostaBO;

    public RespostaAlunoController(RespostaAlunoBO respostaBO) {
        this.respostaBO = respostaBO;
    }

    @GetMapping
    public List<RespostaAluno> listar() {
        return respostaBO.listar();
    }

    @GetMapping("/{id}")
    public RespostaAluno buscar(@PathVariable Long id) {
        return respostaBO.buscar(id);
    }

    /** Exclui a resposta e devolve o XP, se ela estava correta. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        respostaBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
