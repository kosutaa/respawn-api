package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.AlunoBO;
import br.com.fiap.respawn.model.Aluno;
import br.com.fiap.respawn.model.MissaoDiaria;
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
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoBO alunoBO;

    public AlunoController(AlunoBO alunoBO) {
        this.alunoBO = alunoBO;
    }

    @GetMapping
    public List<Aluno> listar() {
        return alunoBO.listar();
    }

    @GetMapping("/{id}")
    public Aluno buscar(@PathVariable Long id) {
        return alunoBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Aluno> cadastrar(@Valid @RequestBody Aluno aluno) {
        Aluno criado = alunoBO.cadastrar(aluno);
        return ResponseEntity.created(URI.create("/alunos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Aluno atualizar(@PathVariable Long id, @Valid @RequestBody Aluno aluno) {
        return alunoBO.atualizar(id, aluno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/missoes")
    public List<MissaoDiaria> missoes(@PathVariable Long id) {
        return alunoBO.missoes(id);
    }
}
