package com.reservas.facisa.mapper;

import org.springframework.stereotype.Component;

import com.reservas.facisa.dto.UsuarioRequestDTO;
import com.reservas.facisa.dto.UsuarioResponseDTO;
import com.reservas.facisa.model.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .build();
    }

    public void updateEntityFromDto(UsuarioRequestDTO dto, Usuario usuario) {
        if (dto == null || usuario == null) {
            return;
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
    }

    public UsuarioResponseDTO toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }
}
