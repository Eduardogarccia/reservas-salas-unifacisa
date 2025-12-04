package com.reservas.facisa.service;

import com.reservas.facisa.dto.SalaRequestDTO;
import com.reservas.facisa.dto.SalaResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.SalaMapper;
import com.reservas.facisa.model.Sala;
import com.reservas.facisa.model.StatusSala;
import com.reservas.facisa.model.TipoSala;
import com.reservas.facisa.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private SalaMapper salaMapper;

    @InjectMocks
    private SalaService salaService;

    private SalaRequestDTO request;
    private Sala salaBase;

    @BeforeEach
    void setUp() {
        request = new SalaRequestDTO();
        request.setNome("Sala 101");
        request.setTipo(TipoSala.SALA_AULA);
        request.setCapacidade(30);
        request.setStatus(StatusSala.ATIVA);

        salaBase = Sala.builder()
                .id(1L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();
    }

    @Test
    void deveCriarSalaComSucesso() {
        when(salaRepository.existsByNomeIgnoreCase("Sala 101")).thenReturn(false);
        when(salaMapper.toEntity(request)).thenReturn(salaBase);
        when(salaRepository.save(any(Sala.class))).thenReturn(salaBase);
        when(salaMapper.toResponse(salaBase)).thenReturn(
                SalaResponseDTO.builder()
                        .id(1L)
                        .nome("Sala 101")
                        .tipo(TipoSala.SALA_AULA)
                        .capacidade(30)
                        .status(StatusSala.ATIVA)
                        .build()
        );

        SalaResponseDTO response = salaService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sala 101", response.getNome());
        assertEquals(30, response.getCapacidade());
        assertEquals(StatusSala.ATIVA, response.getStatus());
    }

    @Test
    void naoDeveCriarSalaComNomeDuplicado() {
        when(salaRepository.existsByNomeIgnoreCase("Sala 101")).thenReturn(true);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> salaService.criar(request)
        );

        assertEquals("Já existe uma sala com esse nome.", ex.getMessage());
        verify(salaRepository, never()).save(any());
    }

    @Test
    void deveListarSalas() {
        when(salaRepository.findAll()).thenReturn(Collections.singletonList(salaBase));
        when(salaMapper.toResponse(salaBase)).thenReturn(
                SalaResponseDTO.builder()
                        .id(1L)
                        .nome("Sala 101")
                        .tipo(TipoSala.SALA_AULA)
                        .capacidade(30)
                        .status(StatusSala.ATIVA)
                        .build()
        );

        List<SalaResponseDTO> response = salaService.listar();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Sala 101", response.get(0).getNome());
    }

    @Test
    void deveBuscarSalaPorIdComSucesso() {
        SalaResponseDTO responseDTO = SalaResponseDTO.builder()
                .id(1L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaBase));
        when(salaMapper.toResponse(salaBase)).thenReturn(responseDTO);

        SalaResponseDTO response = salaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sala 101", response.getNome());
        assertEquals(30, response.getCapacidade());
    }

    @Test
    void deveLancarExcecaoAoBuscarSalaInexistente() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> salaService.buscarPorId(1L)
        );

        assertEquals("Sala não encontrada.", ex.getMessage());
    }

    @Test
    void deveAtualizarSalaComSucesso() {
        Sala salaExistente = Sala.builder()
                .id(1L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        SalaRequestDTO dto = new SalaRequestDTO();
        dto.setNome("Sala 202");
        dto.setTipo(TipoSala.LABORATORIO);
        dto.setCapacidade(45);
        dto.setStatus(StatusSala.ATIVA);

        SalaResponseDTO responseDTO = SalaResponseDTO.builder()
                .id(1L)
                .nome("Sala 202")
                .tipo(TipoSala.LABORATORIO)
                .capacidade(45)
                .status(StatusSala.ATIVA)
                .build();

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente));
        when(salaRepository.existsByNomeIgnoreCase("Sala 202")).thenReturn(false);
        when(salaMapper.toResponse(salaExistente)).thenReturn(responseDTO);

        SalaResponseDTO response = salaService.atualizar(1L, dto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sala 202", response.getNome());
        assertEquals(45, response.getCapacidade());
        assertEquals(TipoSala.LABORATORIO, response.getTipo());

        verify(salaRepository).save(salaExistente);
        verify(salaMapper).updateEntityFromDto(dto, salaExistente);
    }

    @Test
    void naoDeveAtualizarSalaComNomeDuplicado() {
        Sala salaExistente = Sala.builder()
                .id(1L)
                .nome("Sala Original")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        SalaRequestDTO dto = new SalaRequestDTO();
        dto.setNome("Sala Duplicada");
        dto.setTipo(TipoSala.SALA_AULA);
        dto.setCapacidade(30);
        dto.setStatus(StatusSala.ATIVA);

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente));
        when(salaRepository.existsByNomeIgnoreCase("Sala Duplicada")).thenReturn(true);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> salaService.atualizar(1L, dto)
        );

        assertEquals("Já existe outra sala com esse nome.", ex.getMessage());
        verify(salaMapper, never()).updateEntityFromDto(any(), any());
        verify(salaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarSalaInexistente() {
        SalaRequestDTO dto = new SalaRequestDTO();
        dto.setNome("Sala 999");
        dto.setTipo(TipoSala.LABORATORIO);
        dto.setCapacidade(10);
        dto.setStatus(StatusSala.ATIVA);

        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> salaService.atualizar(1L, dto)
        );

        assertEquals("Sala não encontrada.", ex.getMessage());
        verify(salaRepository, never()).save(any());
        verify(salaMapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void deveRemoverSalaComSucesso() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaBase));

        salaService.remover(1L);

        verify(salaRepository).delete(salaBase);
    }

    @Test
    void deveLancarExcecaoAoRemoverSalaInexistente() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> salaService.remover(1L)
        );

        assertEquals("Sala não encontrada.", ex.getMessage());
        verify(salaRepository, never()).delete(any());
    }

    @Test
    void deveBuscarEntityPorIdComSucesso() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaBase));

        Sala entidade = salaService.buscarEntityPorId(1L);

        assertNotNull(entidade);
        assertEquals(1L, entidade.getId());
        assertEquals("Sala 101", entidade.getNome());
        assertEquals(30, entidade.getCapacidade());
    }

    @Test
    void deveLancarExcecaoAoBuscarEntityInexistente() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> salaService.buscarEntityPorId(1L)
        );

        assertEquals("Sala não encontrada.", ex.getMessage());
    }
}
