package com.reservas.facisa.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import com.reservas.facisa.dto.UsuarioRequestDTO;
import com.reservas.facisa.dto.UsuarioResponseDTO;
import com.reservas.facisa.exception.RegraNegocioException;
import com.reservas.facisa.exception.RecursoNaoEncontradoException;
import com.reservas.facisa.mapper.UsuarioMapper;
import com.reservas.facisa.model.Usuario;
import com.reservas.facisa.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new UsuarioRequestDTO();
        request.setNome("João da Silva");
        request.setEmail("joao@exemplo.com");
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao@exemplo.com")
                .build();

        when(usuarioRepository.existsByEmailIgnoreCase("joao@exemplo.com")).thenReturn(false);
        when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                UsuarioResponseDTO.builder()
                        .id(1L)
                        .nome("João da Silva")
                        .email("joao@exemplo.com")
                        .build()
        );

        UsuarioResponseDTO response = usuarioService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João da Silva", response.getNome());
    }

    @Test
    void naoDeveCriarUsuarioComEmailDuplicado() {
        when(usuarioRepository.existsByEmailIgnoreCase("joao@exemplo.com")).thenReturn(true);

        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> usuarioService.criar(request)
        );

        assertEquals("Já existe um usuário com esse e-mail.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1L));
    }

    @Test
    void deveListarUsuarios() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao@exemplo.com")
                .build();

        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                UsuarioResponseDTO.builder()
                        .id(1L)
                        .nome("João da Silva")
                        .email("joao@exemplo.com")
                        .build()
        );

        List<UsuarioResponseDTO> response = usuarioService.listar();

        assertEquals(1, response.size());
        assertEquals("João da Silva", response.get(0).getNome());
    }
}
