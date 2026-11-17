package br.com.fiap.respawn.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada de tratamento de excecoes: transforma cada tipo de erro em uma
 * resposta HTTP com o status correto e um JSON padronizado (ErroResponse).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404: registro nao existe. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of());
    }

    /** 404: rota inexistente. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return resposta(HttpStatus.NOT_FOUND, "Rota nao encontrada", req, List.of());
    }

    /** 400: regra de negocio violada (ex.: resposta passa do tempo da missao). */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> regraDeNegocio(RegraDeNegocioException ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage(), req, List.of());
    }

    /** 400: campos invalidos no corpo da requisicao (anotacoes @NotNull, @Size etc.). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados invalidos na requisicao", req, detalhes);
    }

    /** 400: JSON mal formatado ou com tipo errado (ex.: texto em campo numerico). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisicao invalido ou mal formatado", req, List.of());
    }

    /** 400: parametro da URL com tipo errado (ex.: /alunos/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> parametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return resposta(HttpStatus.BAD_REQUEST, "Parametro invalido: " + ex.getName(), req, List.of());
    }

    /** 405: metodo HTTP nao suportado na rota. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex,
                                                           HttpServletRequest req) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + ex.getMethod() + " nao suportado nesta rota",
                req, List.of());
    }

    /** 409: o banco recusou por integridade (registro com vinculos, valor unico repetido etc.). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        String causa = ex.getMostSpecificCause().getMessage();
        String mensagem = "Operacao recusada pelo banco de dados por violar a integridade dos dados";
        if (causa != null && causa.contains("ORA-02292")) {
            mensagem = "O registro possui vinculos com outros registros e nao pode ser excluido";
        } else if (causa != null && causa.contains("ORA-00001")) {
            mensagem = "Ja existe um registro com esse valor unico (ex.: e-mail ou codigo INEP)";
        }
        return resposta(HttpStatus.CONFLICT, mensagem, req, List.of());
    }

    /** 503: falha de comunicacao com o banco de dados. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErroResponse> banco(DataAccessException ex, HttpServletRequest req) {
        LOG.error("Erro de acesso ao banco em {}", req.getRequestURI(), ex);
        return resposta(HttpStatus.SERVICE_UNAVAILABLE, "Erro ao acessar o banco de dados", req, List.of());
    }

    /** 500: qualquer erro inesperado. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception ex, HttpServletRequest req) {
        LOG.error("Erro inesperado em {}", req.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", req, List.of());
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem,
                                                  HttpServletRequest req, List<String> detalhes) {
        ErroResponse corpo = new ErroResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, req.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
