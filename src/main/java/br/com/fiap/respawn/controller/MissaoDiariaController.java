package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.MissaoDiariaBO;
import br.com.fiap.respawn.bo.RespostaAlunoBO;
import br.com.fiap.respawn.model.MissaoDiaria;
import br.com.fiap.respawn.model.RespostaAluno;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/missoes")
public class MissaoDiariaController {

    private final MissaoDiariaBO missaoBO;
    private final RespostaAlunoBO respostaBO;

    public MissaoDiariaController(MissaoDiariaBO missaoBO, RespostaAlunoBO respostaBO) {
        this.missaoBO = missaoBO;
        this.respostaBO = respostaBO;
    }

    @GetMapping
    public List<MissaoDiaria> listar() {
        return missaoBO.listar();
    }

    @GetMapping("/{id}")
    public MissaoDiaria buscar(@PathVariable Long id) {
        return missaoBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<MissaoDiaria> cadastrar(@Valid @RequestBody MissaoDiaria missao) {
        MissaoDiaria criada = missaoBO.cadastrar(missao);
        return ResponseEntity.created(URI.create("/missoes/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public MissaoDiaria atualizar(@PathVariable Long id, @Valid @RequestBody MissaoDiaria missao) {
        return missaoBO.atualizar(id, missao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        missaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/concluir")
    public MissaoDiaria concluir(@PathVariable Long id) {
        return missaoBO.concluir(id);
    }

    @GetMapping("/{id}/respostas")
    public List<RespostaAluno> respostas(@PathVariable Long id) {
        return respostaBO.listarPorMissao(id);
    }

    @PostMapping("/{id}/respostas")
    public ResponseEntity<RespostaAluno> responder(@PathVariable Long id,
                                                   @Valid @RequestBody RespostaAluno resposta) {
        RespostaAluno criada = respostaBO.registrar(id, resposta);
        return ResponseEntity.created(URI.create("/respostas/" + criada.getId())).body(criada);
    }
}
