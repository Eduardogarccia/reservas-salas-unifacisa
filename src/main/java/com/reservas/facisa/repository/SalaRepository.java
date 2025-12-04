package com.reservas.facisa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.facisa.model.Sala;
import com.reservas.facisa.model.StatusSala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Sala> findByNomeIgnoreCase(String nome);
    
    List<Sala> findByStatus(StatusSala status);

}
