package com.reservas.facisa.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErroResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> errors;
}
