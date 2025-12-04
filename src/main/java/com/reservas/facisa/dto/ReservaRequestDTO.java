package com.reservas.facisa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "O usuário é obrigatório.")
    private Long usuarioId;

    @NotNull(message = "A sala é obrigatória.")
    private Long salaId;

    @NotBlank(message = "A data é obrigatória.")
    private String data;

    @NotBlank(message = "A hora de início é obrigatória.")
    private String horaInicio;

    @NotBlank(message = "A hora de fim é obrigatória.")
    private String horaFim;

    @NotBlank(message = "O motivo é obrigatório.")
    private String motivo;
}
