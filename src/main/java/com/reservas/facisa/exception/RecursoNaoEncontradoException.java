package com.reservas.facisa.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }

    public RecursoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
