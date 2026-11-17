package br.com.fiap.respawn.exception;

/** Lancada quando um registro buscado pelo id nao existe (resposta HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " nao encontrado(a)");
    }
}
