package com.reservas.facisa.dto;

import com.reservas.facisa.model.StatusSala;
import com.reservas.facisa.model.TipoSala;

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
public class SalaResponseDTO {

    private Long id;
    private String nome;
    private TipoSala tipo;
    private Integer capacidade;
    private StatusSala status;
}
