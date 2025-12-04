package com.reservas.facisa.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_reserva_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_reserva_sala"))
    private Sala sala;

    @Column(name = "data_reserva", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "motivo", nullable = false, length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private StatusReserva status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.status = this.status == null ? StatusReserva.ATIVA : this.status;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
