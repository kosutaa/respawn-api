package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.TurmaBO;
import br.com.fiap.respawn.model.Aluno;
import br.com.fiap.respawn.model.ErroPorTopico;
import br.com.fiap.respawn.model.Turma;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaBO turmaBO;

    public TurmaController(TurmaBO turmaBO) {
        this.turmaBO = turmaBO;
    }

    @GetMapping
    public List<Turma> listar() {
        return turmaBO.listar();
    }

    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        return turmaBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Turma> cadastrar(@Valid @RequestBody Turma turma) {
        Turma criada = turmaBO.cadastrar(turma);
        return ResponseEntity.created(URI.create("/turmas/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @Valid @RequestBody Turma turma) {
        return turmaBO.atualizar(id, turma);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        turmaBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /** Alunos da turma, do maior para o menor XP. */
    @GetMapping("/{id}/alunos")
    public List<Aluno> alunos(@PathVariable Long id) {
        return turmaBO.alunos(id);
    }

    /** Painel do professor: topicos em que a turma mais erra. */
    @GetMapping("/{id}/erros-por-topico")
    public List<ErroPorTopico> errosPorTopico(@PathVariable Long id) {
        return turmaBO.errosPorTopico(id);
    }

    /** Busca ativa: alunos sem sequencia ou sem acessar ha mais de N dias (padrao 7). */
    @GetMapping("/{id}/alunos-inativos")
    public List<Aluno> alunosInativos(@PathVariable Long id, @RequestParam(defaultValue = "7") int dias) {
        return turmaBO.alunosInativos(id, dias);
    }
}
