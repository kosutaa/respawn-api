package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.TopicoBO;
import br.com.fiap.respawn.model.Exercicio;
import br.com.fiap.respawn.model.Topico;
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
@RequestMapping("/topicos")
public class TopicoController {

    private final TopicoBO topicoBO;

    public TopicoController(TopicoBO topicoBO) {
        this.topicoBO = topicoBO;
    }

    @GetMapping
    public List<Topico> listar() {
        return topicoBO.listar();
    }

    @GetMapping("/{id}")
    public Topico buscar(@PathVariable Long id) {
        return topicoBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Topico> cadastrar(@Valid @RequestBody Topico topico) {
        Topico criado = topicoBO.cadastrar(topico);
        return ResponseEntity.created(URI.create("/topicos/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public Topico atualizar(@PathVariable Long id, @Valid @RequestBody Topico topico) {
        return topicoBO.atualizar(id, topico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        topicoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /** Exercicios do topico, do mais curto para o mais longo. */
    @GetMapping("/{id}/exercicios")
    public List<Exercicio> exercicios(@PathVariable Long id) {
        return topicoBO.exercicios(id);
    }

    /** Trilha de pre-requisitos ate o topico, na ordem de estudo. */
    @GetMapping("/{id}/trilha")
    public List<Topico> trilha(@PathVariable Long id) {
        return topicoBO.trilha(id);
    }
}
