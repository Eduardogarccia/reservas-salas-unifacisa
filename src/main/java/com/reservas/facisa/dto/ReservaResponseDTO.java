package com.reservas.facisa.dto;

import com.reservas.facisa.model.StatusReserva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private Long id;

    private Long usuarioId;
    private String usuarioNome;

    private Long salaId;
    private String salaNome;

    private String data;
    private String horaInicio;
    private String horaFim;

    private String motivo;

    private StatusReserva status;
}
