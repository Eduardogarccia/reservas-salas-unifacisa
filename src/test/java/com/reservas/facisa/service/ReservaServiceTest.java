package com.reservas.facisa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import com.reservas.facisa.dto.ReservaRequestDTO;
import com.reservas.facisa.dto.ReservaResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.ReservaMapper;
import com.reservas.facisa.mapper.SalaMapper;
import com.reservas.facisa.model.*;
import com.reservas.facisa.repository.ReservaRepository;
import com.reservas.facisa.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    // Usaremos o mapper real (sem mock) para garantir conversão real
    private ReservaMapper reservaMapper = new ReservaMapper();

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SalaService salaService;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private SalaMapper salaMapper;

    private ReservaService reservaService;

    private Usuario usuario;
    private Sala sala;
    private ReservaRequestDTO requestFuturo;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(
                reservaRepository,
                reservaMapper,
                usuarioService,
                salaService,
                salaRepository,
                salaMapper
        );

        usuario = Usuario.builder()
                .id(1L)
                .nome("Professor X")
                .email("prof@facisa.edu")
                .build();

        sala = Sala.builder()
                .id(10L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(40)
                .status(StatusSala.ATIVA)
                .build();

        LocalDate dataFutura = LocalDate.now().plusDays(1);

        requestFuturo = new ReservaRequestDTO();
        requestFuturo.setUsuarioId(1L);
        requestFuturo.setSalaId(10L);
        requestFuturo.setData(dataFutura.toString()); // yyyy-MM-dd
        requestFuturo.setHoraInicio("10:00");
        requestFuturo.setHoraFim("12:00");
        requestFuturo.setMotivo("Aula de Algoritmos");
    }

    @Test
    void deveCriarReservaComSucesso() {
        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);
        when(reservaRepository.findReservasConflitantes(
                eq(10L),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class),
                eq(StatusReserva.ATIVA)
        )).thenReturn(Collections.emptyList());

        ReservaResponseDTO response = reservaService.criar(requestFuturo);

        assertNotNull(response);
        assertEquals("Sala 101", response.getSalaNome());
        assertEquals("Professor X", response.getUsuarioNome());

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());
        Reserva salva = captor.getValue();

        assertEquals(LocalTime.of(10, 0), salva.getHoraInicio());
        assertEquals(LocalTime.of(12, 0), salva.getHoraFim());
        assertEquals(StatusReserva.ATIVA, salva.getStatus());
    }

    @Test
    void naoDeveCriarReservaEmSalaInativa() {
        sala.setStatus(StatusSala.INATIVA);

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFuturo)
        );

        assertEquals("Não é possível reservar uma sala inativa.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarReservaComHoraFimMenorOuIgualInicio() {
        requestFuturo.setHoraFim("10:00"); // igual ao início

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFuturo)
        );

        assertEquals("A hora de fim deve ser maior que a hora de início.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarReservaNoPassado() {
        LocalDate dataPassada = LocalDate.now().minusDays(1);
        requestFuturo.setData(dataPassada.toString());

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFuturo)
        );

        assertEquals("Não é possível criar ou alterar reservas no passado.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarReservaComSobreposicao() {
        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        Reserva reservaExistente = Reserva.builder()
                .id(99L)
                .usuario(usuario)
                .sala(sala)
                .data(LocalDate.parse(requestFuturo.getData()))
                .horaInicio(LocalTime.of(9, 0))
                .horaFim(LocalTime.of(11, 0))
                .motivo("Outra aula")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findReservasConflitantes(
                eq(10L),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class),
                eq(StatusReserva.ATIVA)
        )).thenReturn(Collections.singletonList(reservaExistente));

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFuturo)
        );

        assertEquals("Já existe uma reserva para essa sala nesse horário.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveCancelarReservaAntesDoInicio() {
        LocalDate dataFutura = LocalDate.now().plusDays(1);

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(dataFutura)
                .horaInicio(LocalTime.of(10, 0))
                .horaFim(LocalTime.of(12, 0))
                .motivo("Aula")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.cancelar(1L);

        assertEquals(StatusReserva.CANCELADA, reserva.getStatus());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void naoDeveCancelarReservaQueJaIniciouOuPassou() {
        LocalDate dataHoje = LocalDate.now();
        LocalTime horaPassada = LocalTime.now().minusHours(1);

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(dataHoje)
                .horaInicio(horaPassada)
                .horaFim(horaPassada.plusHours(1))
                .motivo("Aula")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.cancelar(1L)
        );

        assertEquals("Cancelamentos só podem ocorrer antes do horário de início da reserva.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaInexistente() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> reservaService.cancelar(1L));
    }
}
