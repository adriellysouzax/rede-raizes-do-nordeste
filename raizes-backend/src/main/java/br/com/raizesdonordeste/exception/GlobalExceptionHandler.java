
package br.com.raizesdonordeste.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(
            IllegalArgumentException ex) {

        HttpStatus status = ex.getMessage() != null
                && ex.getMessage().startsWith("Estoque insuficiente:")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "erro", status.getReasonPhrase(),
                "mensagem", ex.getMessage() != null
                        ? ex.getMessage()
                        : "Requisição inválida"));
    }
}
