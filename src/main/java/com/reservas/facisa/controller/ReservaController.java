package com.reservas.facisa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.facisa.dto.ReservaRequestDTO;
import com.reservas.facisa.dto.ReservaResponseDTO;
import com.reservas.facisa.service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO reserva = reservaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar(
            @RequestParam(name = "sala_id", required = false) Long salaId,
            @RequestParam(name = "usuario_id", required = false) Long usuarioId,
            @RequestParam(name = "data", required = false) String data) {

        if (salaId != null && data != null) {
            return ResponseEntity.ok(reservaService.listarPorSalaEData(salaId, data));
        }

        if (usuarioId != null) {
            return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId));
        }

        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO atualizada = reservaService.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
