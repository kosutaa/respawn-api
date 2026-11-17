package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.ExercicioBO;
import br.com.fiap.respawn.model.Exercicio;
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
@RequestMapping("/exercicios")
public class ExercicioController {

    private final ExercicioBO exercicioBO;

    public ExercicioController(ExercicioBO exercicioBO) {
        this.exercicioBO = exercicioBO;
    }

    @GetMapping
    public List<Exercicio> listar() {
        return exercicioBO.listar();
    }

    @GetMapping("/{id}")
    public Exercicio buscar(@PathVariable Long id) {
        return exercicioBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Exercicio> cadastrar(@Valid @RequestBody Exercicio exercicio) {
        Exercicio criado = exercicioBO.cadastrar(exercicio);
        return ResponseEntity.created(URI.create("/exercicios/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Exercicio atualizar(@PathVariable Long id, @Valid @RequestBody Exercicio exercicio) {
        return exercicioBO.atualizar(id, exercicio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        exercicioBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
