package com.reservas.facisa.exception;

public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String message) {
        super(message);
    }

    public RegraNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
