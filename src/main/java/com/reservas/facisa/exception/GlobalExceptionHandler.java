package com.reservas.facisa.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex,
                                                                   HttpServletRequest request) {

        ErroResponse body = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Recurso não encontrado")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraNegocio(RegraNegocioException ex,
                                                           HttpServletRequest request) {

        ErroResponse body = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Regra de negócio violada")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request) {

        List<String> erros = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        ErroResponse body = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Erro de validação")
                .message("Um ou mais campos estão inválidos.")
                .path(request.getRequestURI())
                .errors(erros)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex,
                                                      HttpServletRequest request) {

        ErroResponse body = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Erro interno no servidor")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
