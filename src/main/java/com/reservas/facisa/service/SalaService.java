package com.reservas.facisa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.facisa.dto.SalaRequestDTO;
import com.reservas.facisa.dto.SalaResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.SalaMapper;
import com.reservas.facisa.model.Sala;
import com.reservas.facisa.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    public SalaService(SalaRepository salaRepository, SalaMapper salaMapper) {
        this.salaRepository = salaRepository;
        this.salaMapper = salaMapper;
    }

    @Transactional
    public SalaResponseDTO criar(SalaRequestDTO dto) {

        if (salaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RegraNegocioException("Já existe uma sala com esse nome.");
        }

        Sala sala = salaMapper.toEntity(dto);
        salaRepository.save(sala);

        return salaMapper.toResponse(sala);
    }

    public List<SalaResponseDTO> listar() {
        return salaRepository.findAll()
                .stream()
                .map(salaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SalaResponseDTO buscarPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));

        return salaMapper.toResponse(sala);
    }

    @Transactional
    public SalaResponseDTO atualizar(Long id, SalaRequestDTO dto) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));

        if (!sala.getNome().equalsIgnoreCase(dto.getNome()) &&
            salaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RegraNegocioException("Já existe outra sala com esse nome.");
        }

        salaMapper.updateEntityFromDto(dto, sala);
        salaRepository.save(sala);

        return salaMapper.toResponse(sala);
    }

    @Transactional
    public void remover(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));

        salaRepository.delete(sala);
    }

    public Sala buscarEntityPorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));
    }
}
