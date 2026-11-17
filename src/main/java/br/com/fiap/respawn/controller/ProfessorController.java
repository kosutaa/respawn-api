package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.ProfessorBO;
import br.com.fiap.respawn.model.Professor;
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
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorBO professorBO;

    public ProfessorController(ProfessorBO professorBO) {
        this.professorBO = professorBO;
    }

    @GetMapping
    public List<Professor> listar() {
        return professorBO.listar();
    }

    @GetMapping("/{id}")
    public Professor buscar(@PathVariable Long id) {
        return professorBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Professor> cadastrar(@Valid @RequestBody Professor professor) {
        Professor criado = professorBO.cadastrar(professor);
        return ResponseEntity.created(URI.create("/professores/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Professor atualizar(@PathVariable Long id, @Valid @RequestBody Professor professor) {
        return professorBO.atualizar(id, professor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        professorBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
