package br.com.fiap.respawn.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Corpo JSON padronizado devolvido em qualquer erro da API. */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes) {
}
