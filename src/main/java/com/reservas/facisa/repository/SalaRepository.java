package com.reservas.facisa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.facisa.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Sala> findByNomeIgnoreCase(String nome);
}
