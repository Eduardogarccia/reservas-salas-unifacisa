package com.reservas.facisa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private SalaMapper salaMapper;

    @InjectMocks
    private SalaService salaService;

    private SalaRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new SalaRequestDTO();
        request.setNome("Sala 101");
        request.setTipo(TipoSala.SALA_AULA);
        request.setCapacidade(30);
        request.setStatus(StatusSala.ATIVA);
    }

    @Test
    void deveCriarSalaComSucesso() {
        Sala sala = Sala.builder()
                .id(1L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        when(salaRepository.existsByNomeIgnoreCase("Sala 101")).thenReturn(false);
        when(salaMapper.toEntity(request)).thenReturn(sala);
        when(salaRepository.save(any(Sala.class))).thenReturn(sala);
        when(salaMapper.toResponse(sala)).thenReturn(
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
    void deveLancarExcecaoAoBuscarSalaInexistente() {
        when(salaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> salaService.buscarPorId(1L));
    }

    @Test
    void deveListarSalas() {
        Sala sala = Sala.builder()
                .id(1L)
                .nome("Sala 101")
                .tipo(TipoSala.SALA_AULA)
                .capacidade(30)
                .status(StatusSala.ATIVA)
                .build();

        when(salaRepository.findAll()).thenReturn(Collections.singletonList(sala));
        when(salaMapper.toResponse(sala)).thenReturn(
                SalaResponseDTO.builder()
                        .id(1L)
                        .nome("Sala 101")
                        .tipo(TipoSala.SALA_AULA)
                        .capacidade(30)
                        .status(StatusSala.ATIVA)
                        .build()
        );

        List<SalaResponseDTO> response = salaService.listar();

        assertEquals(1, response.size());
        assertEquals("Sala 101", response.get(0).getNome());
    }
}
