package br.com.fiap.respawn.exception;

/** Lancada quando uma operacao viola uma regra de negocio do Respawn (resposta HTTP 400). */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
