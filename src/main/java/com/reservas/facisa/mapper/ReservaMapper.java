package com.reservas.facisa.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.reservas.facisa.dto.ReservaRequestDTO;
import com.reservas.facisa.dto.ReservaResponseDTO;
import com.reservas.facisa.model.Reserva;
import com.reservas.facisa.model.Sala;
import com.reservas.facisa.model.StatusReserva;
import com.reservas.facisa.model.Usuario;

@Component
public class ReservaMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Reserva toEntity(ReservaRequestDTO dto, Usuario usuario, Sala sala) {
        if (dto == null || usuario == null || sala == null) {
            return null;
        }

        LocalDate data = LocalDate.parse(dto.getData(), DATE_FORMATTER);
        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio(), TIME_FORMATTER);
        LocalTime horaFim = LocalTime.parse(dto.getHoraFim(), TIME_FORMATTER);

        return Reserva.builder()
                .usuario(usuario)
                .sala(sala)
                .data(data)
                .horaInicio(horaInicio)
                .horaFim(horaFim)
                .motivo(dto.getMotivo())
                .status(StatusReserva.ATIVA)
                .build();
    }

    public void updateEntityFromDto(ReservaRequestDTO dto, Reserva reserva, Usuario usuario, Sala sala) {
        if (dto == null || reserva == null) {
            return;
        }

        if (usuario != null) {
            reserva.setUsuario(usuario);
        }
        if (sala != null) {
            reserva.setSala(sala);
        }

        LocalDate data = LocalDate.parse(dto.getData(), DATE_FORMATTER);
        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio(), TIME_FORMATTER);
        LocalTime horaFim = LocalTime.parse(dto.getHoraFim(), TIME_FORMATTER);

        reserva.setData(data);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFim(horaFim);
        reserva.setMotivo(dto.getMotivo());
    }

    public ReservaResponseDTO toResponse(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        String data = reserva.getData() != null ? reserva.getData().format(DATE_FORMATTER) : null;
        String horaInicio = reserva.getHoraInicio() != null ? reserva.getHoraInicio().format(TIME_FORMATTER) : null;
        String horaFim = reserva.getHoraFim() != null ? reserva.getHoraFim().format(TIME_FORMATTER) : null;

        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .usuarioId(reserva.getUsuario() != null ? reserva.getUsuario().getId() : null)
                .usuarioNome(reserva.getUsuario() != null ? reserva.getUsuario().getNome() : null)
                .salaId(reserva.getSala() != null ? reserva.getSala().getId() : null)
                .salaNome(reserva.getSala() != null ? reserva.getSala().getNome() : null)
                .data(data)
                .horaInicio(horaInicio)
                .horaFim(horaFim)
                .motivo(reserva.getMotivo())
                .status(reserva.getStatus())
                .build();
    }
}
