package br.com.fiap.respawn.controller;

import br.com.fiap.respawn.bo.EscolaBO;
import br.com.fiap.respawn.model.Escola;
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
@RequestMapping("/escolas")
public class EscolaController {

    private final EscolaBO escolaBO;

    public EscolaController(EscolaBO escolaBO) {
        this.escolaBO = escolaBO;
    }

    @GetMapping
    public List<Escola> listar() {
        return escolaBO.listar();
    }

    @GetMapping("/{id}")
    public Escola buscar(@PathVariable Long id) {
        return escolaBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Escola> cadastrar(@Valid @RequestBody Escola escola) {
        Escola criada = escolaBO.cadastrar(escola);
        return ResponseEntity.created(URI.create("/escolas/" + criada.getId())).body(criada);
    }

    @PutMapping("/{id}")
    public Escola atualizar(@PathVariable Long id, @Valid @RequestBody Escola escola) {
        return escolaBO.atualizar(id, escola);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        escolaBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
