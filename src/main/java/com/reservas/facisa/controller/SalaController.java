package com.reservas.facisa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reservas.facisa.dto.SalaRequestDTO;
import com.reservas.facisa.dto.SalaResponseDTO;
import com.reservas.facisa.service.ReservaService;
import com.reservas.facisa.service.SalaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;
    private final ReservaService reservaService;

    public SalaController(SalaService salaService, ReservaService reservaService) {
        this.salaService = salaService;
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<SalaResponseDTO> criar(@Valid @RequestBody SalaRequestDTO dto) {
        SalaResponseDTO sala = salaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(sala);
    }

    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listar() {
        return ResponseEntity.ok(salaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody SalaRequestDTO dto) {
        SalaResponseDTO salaAtualizada = salaService.atualizar(id, dto);
        return ResponseEntity.ok(salaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        salaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<SalaResponseDTO>> listarSalasDisponiveis(
            @RequestParam("data") String data,
            @RequestParam("hora_inicio") String horaInicio,
            @RequestParam("hora_fim") String horaFim) {

        List<SalaResponseDTO> disponiveis = reservaService.listarSalasDisponiveis(data, horaInicio, horaFim);
        return ResponseEntity.ok(disponiveis);
    }
}
