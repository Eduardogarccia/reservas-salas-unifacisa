package com.reservas.facisa.mapper;

import org.springframework.stereotype.Component;

import com.reservas.facisa.dto.SalaRequestDTO;
import com.reservas.facisa.dto.SalaResponseDTO;
import com.reservas.facisa.model.Sala;

@Component
public class SalaMapper {

    public Sala toEntity(SalaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Sala.builder()
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .capacidade(dto.getCapacidade())
                .status(dto.getStatus())
                .build();
    }

    public void updateEntityFromDto(SalaRequestDTO dto, Sala sala) {
        if (dto == null || sala == null) {
            return;
        }

        sala.setNome(dto.getNome());
        sala.setTipo(dto.getTipo());
        sala.setCapacidade(dto.getCapacidade());
        sala.setStatus(dto.getStatus());
    }

    public SalaResponseDTO toResponse(Sala sala) {
        if (sala == null) {
            return null;
        }

        return SalaResponseDTO.builder()
                .id(sala.getId())
                .nome(sala.getNome())
                .tipo(sala.getTipo())
                .capacidade(sala.getCapacidade())
                .status(sala.getStatus())
                .build();
    }
}
