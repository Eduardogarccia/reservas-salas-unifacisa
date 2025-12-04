package com.reservas.facisa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.facisa.dto.UsuarioRequestDTO;
import com.reservas.facisa.dto.UsuarioResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.UsuarioMapper;
import com.reservas.facisa.model.Usuario;
import com.reservas.facisa.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail.");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!usuario.getEmail().equalsIgnoreCase(dto.getEmail()) &&
            usuarioRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new RegraNegocioException("Já existe outro usuário com esse e-mail.");
        }

        usuarioMapper.updateEntityFromDto(dto, usuario);
        usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        usuarioRepository.delete(usuario);
    }

    public Usuario buscarEntityPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
