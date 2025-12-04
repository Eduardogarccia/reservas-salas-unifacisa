package com.reservas.facisa.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "salas",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sala_nome", columnNames = "nome")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoSala tipo;

    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusSala status;
}