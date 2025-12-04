package com.reservas.facisa.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservas.facisa.model.Reserva;
import com.reservas.facisa.model.StatusReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findBySalaIdAndData(Long salaId, LocalDate data);

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findBySalaIdAndDataAndStatus(Long salaId, LocalDate data, StatusReserva status);

    @Query("""
           SELECT r
           FROM Reserva r
           WHERE r.sala.id = :salaId
             AND r.data = :data
             AND r.status = :status
             AND (:horaInicio < r.horaFim AND :horaFim > r.horaInicio)
           """)
    List<Reserva> findReservasConflitantes(
            @Param("salaId") Long salaId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("status") StatusReserva status
    );
}
