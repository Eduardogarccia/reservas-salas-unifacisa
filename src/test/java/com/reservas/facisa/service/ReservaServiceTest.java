package com.reservas.facisa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.reservas.facisa.dto.ReservaRequestDTO;
import com.reservas.facisa.dto.ReservaResponseDTO;
import com.reservas.facisa.dto.SalaResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.ReservaMapper;
import com.reservas.facisa.mapper.SalaMapper;
import com.reservas.facisa.model.Reserva;
import com.reservas.facisa.model.Sala;
import com.reservas.facisa.model.StatusReserva;
import com.reservas.facisa.model.StatusSala;
import com.reservas.facisa.model.TipoSala;
import com.reservas.facisa.model.Usuario;
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

    private final ReservaMapper reservaMapper = new ReservaMapper();

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
    private ReservaRequestDTO requestFutura;

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

        requestFutura = new ReservaRequestDTO();
        requestFutura.setUsuarioId(1L);
        requestFutura.setSalaId(10L);
        requestFutura.setData(dataFutura.toString());
        requestFutura.setHoraInicio("10:00");
        requestFutura.setHoraFim("12:00");
        requestFutura.setMotivo("Aula de Algoritmos");
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

        ReservaResponseDTO response = reservaService.criar(requestFutura);

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
                () -> reservaService.criar(requestFutura)
        );

        assertEquals("Não é possível reservar uma sala inativa.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarReservaComHoraFimMenorOuIgualInicio() {
        requestFutura.setHoraFim("10:00");

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFutura)
        );

        assertEquals("A hora de fim deve ser maior que a hora de início.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarReservaNoPassado() {
        LocalDate dataPassada = LocalDate.now().minusDays(1);
        requestFutura.setData(dataPassada.toString());

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> reservaService.criar(requestFutura)
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
                .data(LocalDate.parse(requestFutura.getData()))
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
                () -> reservaService.criar(requestFutura)
        );

        assertEquals("Já existe uma reserva para essa sala nesse horário.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveBuscarReservaPorIdComSucesso() {
        LocalDate data = LocalDate.now().plusDays(3);

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(data)
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(10, 0))
                .motivo("Monitoria")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        ReservaResponseDTO response = reservaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Professor X", response.getUsuarioNome());
        assertEquals("Sala 101", response.getSalaNome());
        assertEquals("Monitoria", response.getMotivo());
    }

    @Test
    void deveLancarExcecaoAoBuscarReservaInexistente() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> reservaService.buscarPorId(1L));
    }

    @Test
    void deveAtualizarReservaComSucesso() {
        LocalDate dataOriginal = LocalDate.now().plusDays(2);

        Reserva reservaExistente = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(dataOriginal)
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(10, 0))
                .motivo("Reserva antiga")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.findReservasConflitantes(
                eq(10L),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class),
                eq(StatusReserva.ATIVA)
        )).thenReturn(Collections.emptyList());

        ReservaRequestDTO atualizacao = new ReservaRequestDTO();
        atualizacao.setUsuarioId(1L);
        atualizacao.setSalaId(10L);
        atualizacao.setData(dataOriginal.plusDays(1).toString());
        atualizacao.setHoraInicio("14:00");
        atualizacao.setHoraFim("16:00");
        atualizacao.setMotivo("Reserva atualizada");

        when(usuarioService.buscarEntityPorId(1L)).thenReturn(usuario);
        when(salaService.buscarEntityPorId(10L)).thenReturn(sala);

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservaResponseDTO response = reservaService.atualizar(1L, atualizacao);

        assertNotNull(response);
        assertEquals("Reserva atualizada", response.getMotivo());
        assertEquals("14:00", response.getHoraInicio());
        assertEquals("16:00", response.getHoraFim());
        assertEquals(dataOriginal.plusDays(1).toString(), response.getData());
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

    @Test
    void deveListarTodasAsReservas() {
        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(LocalDate.parse(requestFutura.getData()))
                .horaInicio(LocalTime.of(10, 0))
                .horaFim(LocalTime.of(12, 0))
                .motivo("Aula de Algoritmos")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findAll()).thenReturn(Collections.singletonList(reserva));

        List<ReservaResponseDTO> lista = reservaService.listar();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Sala 101", lista.get(0).getSalaNome());
        assertEquals("Professor X", lista.get(0).getUsuarioNome());
    }

    @Test
    void deveListarSalasDisponiveisPeriodoSemConflitos() {
        LocalDate data = LocalDate.now().plusDays(5);

        Sala sala1 = Sala.builder()
                .id(10L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(40)
                .status(StatusSala.ATIVA)
                .build();

        Sala sala2 = Sala.builder()
                .id(20L)
                .nome("Sala 202")
                .tipo(TipoSala.LABORATORIO)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        Reserva reservaConflitante = Reserva.builder()
                .id(99L)
                .usuario(usuario)
                .sala(sala2)
                .data(data)
                .horaInicio(LocalTime.of(9, 0))
                .horaFim(LocalTime.of(10, 0))
                .status(StatusReserva.ATIVA)
                .motivo("Conflito")
                .build();
 
        SalaResponseDTO salaDTO1 = SalaResponseDTO.builder()
                .id(10L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(40)
                .status(StatusSala.ATIVA)
                .build();


        List<SalaResponseDTO> disponiveis = reservaService.listarSalasDisponiveis(
                data.toString(), "10:00", "12:00"
        );

        assertNotNull(disponiveis);
    }

    @Test
    void deveListarReservasPorSalaEDataComResultado() {
        LocalDate data = LocalDate.now().plusDays(2);

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(data)
                .horaInicio(LocalTime.of(10, 0))
                .horaFim(LocalTime.of(11, 0))
                .motivo("Reunião")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findBySalaIdAndData(10L, data))
                .thenReturn(Collections.singletonList(reserva));

        List<ReservaResponseDTO> lista =
                reservaService.listarPorSalaEData(10L, data.toString());

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Sala 101", lista.get(0).getSalaNome());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaReservasPorSalaEData() {
        LocalDate data = LocalDate.now().plusDays(2);

        when(reservaRepository.findBySalaIdAndData(10L, data))
                .thenReturn(Collections.emptyList());

        List<ReservaResponseDTO> lista =
                reservaService.listarPorSalaEData(10L, data.toString());

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    void deveListarReservasPorUsuario() {
        LocalDate data = LocalDate.now().plusDays(3);

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuario(usuario)
                .sala(sala)
                .data(data)
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(10, 0))
                .motivo("Monitoria")
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findByUsuarioId(1L))
                .thenReturn(Collections.singletonList(reserva));

        List<ReservaResponseDTO> lista = reservaService.listarPorUsuario(1L);

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Professor X", lista.get(0).getUsuarioNome());
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemReservas() {
        when(reservaRepository.findByUsuarioId(1L))
                .thenReturn(Collections.emptyList());

        List<ReservaResponseDTO> lista = reservaService.listarPorUsuario(1L);

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }
    
    @Test
    void deveRetornarVerdadeiroQuandoSalaEstaDisponivel() {
        Sala salaTeste = Sala.builder()
                .id(10L)
                .nome("Sala Livre")
                .status(StatusSala.ATIVA)
                .build();

        when(reservaRepository.findReservasConflitantes(
                eq(10L),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class),
                eq(StatusReserva.ATIVA)
        )).thenReturn(Collections.emptyList());

        boolean disponivel = reservaService.estaDisponivel(
                salaTeste,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        assertTrue(disponivel);
    }

    @Test
    void deveRetornarFalsoQuandoSalaNaoEstaDisponivel() {
        Sala salaTeste = Sala.builder()
                .id(10L)
                .nome("Sala Ocupada")
                .status(StatusSala.ATIVA)
                .build();

        Reserva reservaConflito = Reserva.builder()
                .id(88L)
                .sala(salaTeste)
                .data(LocalDate.now().plusDays(1))
                .horaInicio(LocalTime.of(9, 0))
                .horaFim(LocalTime.of(11, 0))
                .status(StatusReserva.ATIVA)
                .build();

        when(reservaRepository.findReservasConflitantes(
                eq(10L),
                any(LocalDate.class),
                any(LocalTime.class),
                any(LocalTime.class),
                eq(StatusReserva.ATIVA)
        )).thenReturn(List.of(reservaConflito));

        boolean disponivel = reservaService.estaDisponivel(
                salaTeste,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        assertFalse(disponivel);
    }


    
}
