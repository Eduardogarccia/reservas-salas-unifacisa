package com.reservas.facisa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservas.facisa.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);
}
