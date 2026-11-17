package br.com.fiap.respawn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pagina inicial da API: confirma que o servico esta no ar e lista os recursos. */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("projeto", "Respawn - Global Solution 2026.2 (FIAP CitizenTech)");
        info.put("descricao", "Microlearning gamificado para reforco escolar em escolas publicas");
        info.put("status", "online");
        info.put("documentacao", "/swagger-ui.html");
        info.put("recursos", List.of("/escolas", "/professores", "/turmas", "/alunos",
                "/topicos", "/exercicios", "/missoes", "/respostas"));
        return info;
    }
}
