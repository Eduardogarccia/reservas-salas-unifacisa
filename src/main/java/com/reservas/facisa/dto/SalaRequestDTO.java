package com.reservas.facisa.dto;

import com.reservas.facisa.model.StatusSala;
import com.reservas.facisa.model.TipoSala;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalaRequestDTO {

    @NotBlank(message = "O nome da sala é obrigatório.")
    private String nome;

    @NotNull(message = "O tipo da sala é obrigatório.")
    private TipoSala tipo;

    @NotNull(message = "A capacidade da sala é obrigatória.")
    @Min(value = 1, message = "A capacidade mínima deve ser 1.")
    private Integer capacidade;

    @NotNull(message = "O status da sala é obrigatório.")
    private StatusSala status;
}
