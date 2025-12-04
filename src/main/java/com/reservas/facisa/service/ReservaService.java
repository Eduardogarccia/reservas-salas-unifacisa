package com.reservas.facisa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.reservas.facisa.model.Usuario;
import com.reservas.facisa.repository.ReservaRepository;
import com.reservas.facisa.repository.SalaRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;
    private final UsuarioService usuarioService;
    private final SalaService salaService;
    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ReservaService(ReservaRepository reservaRepository,
                          ReservaMapper reservaMapper,
                          UsuarioService usuarioService,
                          SalaService salaService,
                          SalaRepository salaRepository,
                          SalaMapper salaMapper) {
        this.reservaRepository = reservaRepository;
        this.reservaMapper = reservaMapper;
        this.usuarioService = usuarioService;
        this.salaService = salaService;
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
    }

    @Transactional
    public ReservaResponseDTO criar(ReservaRequestDTO dto) {

        Usuario usuario = usuarioService.buscarEntityPorId(dto.getUsuarioId());
        Sala sala = salaService.buscarEntityPorId(dto.getSalaId());

        if (sala.getStatus() == StatusSala.INATIVA) {
            throw new RegraNegocioException("Não é possível reservar uma sala inativa.");
        }

        Reserva reserva = reservaMapper.toEntity(dto, usuario, sala);

        validarReserva(reserva, null);

        reservaRepository.save(reserva);

        return reservaMapper.toResponse(reserva);
    }

    public List<ReservaResponseDTO> listar() {
        return reservaRepository.findAll()
                .stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada."));
        return reservaMapper.toResponse(reserva);
    }

    public List<ReservaResponseDTO> listarPorSalaEData(Long salaId, String dataStr) {
        LocalDate data = LocalDate.parse(dataStr, DATE_FORMATTER);

        List<Reserva> reservas = reservaRepository.findBySalaIdAndData(salaId, data);

        return reservas.stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> listarPorUsuario(Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);

        return reservas.stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaResponseDTO atualizar(Long id, ReservaRequestDTO dto) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada."));

        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Não é possível alterar uma reserva cancelada.");
        }

        LocalDateTime inicioAtual = LocalDateTime.of(reserva.getData(), reserva.getHoraInicio());
        if (!inicioAtual.isAfter(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível alterar uma reserva que já começou ou já passou.");
        }

        Usuario usuario = usuarioService.buscarEntityPorId(dto.getUsuarioId());
        Sala sala = salaService.buscarEntityPorId(dto.getSalaId());

        if (sala.getStatus() == StatusSala.INATIVA) {
            throw new RegraNegocioException("Não é possível reservar uma sala inativa.");
        }

        reservaMapper.updateEntityFromDto(dto, reserva, usuario, sala);

        validarReserva(reserva, reserva.getId());

        reservaRepository.save(reserva);

        return reservaMapper.toResponse(reserva);
    }

    @Transactional
    public void cancelar(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada."));

        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("A reserva já está cancelada.");
        }

        LocalDateTime inicio = LocalDateTime.of(reserva.getData(), reserva.getHoraInicio());
        LocalDateTime agora = LocalDateTime.now();

        if (!inicio.isAfter(agora)) {
            throw new RegraNegocioException("Cancelamentos só podem ocorrer antes do horário de início da reserva.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    public List<SalaResponseDTO> listarSalasDisponiveis(String dataStr, String horaInicioStr, String horaFimStr) {

        LocalDate data = LocalDate.parse(dataStr, DATE_FORMATTER);
        LocalTime horaInicio = LocalTime.parse(horaInicioStr, TIME_FORMATTER);
        LocalTime horaFim = LocalTime.parse(horaFimStr, TIME_FORMATTER);

        if (!horaFim.isAfter(horaInicio)) {
            throw new RegraNegocioException("A hora de fim deve ser maior que a hora de início.");
        }

        List<Sala> salasAtivas = salaRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusSala.ATIVA)
                .collect(Collectors.toList());

        return salasAtivas.stream()
                .filter(sala -> estaDisponivel(sala, data, horaInicio, horaFim))
                .map(salaMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validarReserva(Reserva reserva, Long idReservaIgnorar) {

        if (!reserva.getHoraFim().isAfter(reserva.getHoraInicio())) {
            throw new RegraNegocioException("A hora de fim deve ser maior que a hora de início.");
        }

        LocalDateTime inicio = LocalDateTime.of(reserva.getData(), reserva.getHoraInicio());
        LocalDateTime agora = LocalDateTime.now();

        if (!inicio.isAfter(agora)) {
            throw new RegraNegocioException("Não é possível criar ou alterar reservas no passado.");
        }

        List<Reserva> conflitos = reservaRepository.findReservasConflitantes(
                reserva.getSala().getId(),
                reserva.getData(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                StatusReserva.ATIVA
        );

        boolean existeOutroConflito = conflitos.stream()
                .anyMatch(r -> idReservaIgnorar == null || !r.getId().equals(idReservaIgnorar));

        if (existeOutroConflito) {
            throw new RegraNegocioException("Já existe uma reserva para essa sala nesse horário.");
        }
    }

    public boolean estaDisponivel(Sala sala, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {

        List<Reserva> conflitos = reservaRepository.findReservasConflitantes(
                sala.getId(),
                data,
                horaInicio,
                horaFim,
                StatusReserva.ATIVA
        );

        return conflitos.isEmpty();
    }
}