package com.reservas.facisa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.reservas.facisa.dto.UsuarioRequestDTO;
import com.reservas.facisa.dto.UsuarioResponseDTO;
import com.reservas.facisa.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO atualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
