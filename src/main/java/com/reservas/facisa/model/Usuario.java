package com.reservas.facisa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do usuário é obrigatório.")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O e-mail do usuário é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
}
