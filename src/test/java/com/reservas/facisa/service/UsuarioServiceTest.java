package com.reservas.facisa.service;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO request;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        request = new UsuarioRequestDTO();
        request.setNome("João da Silva");
        request.setEmail("joao@exemplo.com");

        usuario = Usuario.builder()
                .id(1L)
                .nome("João da Silva")
                .email("joao@exemplo.com")
                .build();
    }

    @Test
    void deveCriarUsuarioComSucesso() {
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
        assertEquals("joao@exemplo.com", response.getEmail());
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
    void deveBuscarUsuarioPorIdComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                UsuarioResponseDTO.builder()
                        .id(1L)
                        .nome("João da Silva")
                        .email("joao@exemplo.com")
                        .build()
        );

        UsuarioResponseDTO response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João da Silva", response.getNome());
        assertEquals("joao@exemplo.com", response.getEmail());
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1L));
    }

    @Test
    void deveListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                UsuarioResponseDTO.builder()
                        .id(1L)
                        .nome("João da Silva")
                        .email("joao@exemplo.com")
                        .build()
        );

        List<UsuarioResponseDTO> response = usuarioService.listar();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("João da Silva", response.get(0).getNome());
    }

    @Test
    void deveRemoverUsuarioComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.remover(1L);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deveLancarExcecaoAoRemoverUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> usuarioService.remover(1L));

        verify(usuarioRepository, never()).delete(any());
    }
    
 @Test
 void deveAtualizarUsuarioComSucesso() {
     Usuario usuarioExistente = Usuario.builder()
             .id(1L)
             .nome("João da Silva")
             .email("joao@exemplo.com")
             .build();

     UsuarioRequestDTO dtoAtualizado = new UsuarioRequestDTO();
     dtoAtualizado.setNome("João Atualizado");
     dtoAtualizado.setEmail("novo@exemplo.com");

     when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
     when(usuarioRepository.existsByEmailIgnoreCase("novo@exemplo.com")).thenReturn(false);

     when(usuarioMapper.toResponse(usuarioExistente)).thenReturn(
             UsuarioResponseDTO.builder()
                     .id(1L)
                     .nome("João Atualizado")
                     .email("novo@exemplo.com")
                     .build()
     );

     UsuarioResponseDTO response = usuarioService.atualizar(1L, dtoAtualizado);

     assertNotNull(response);
     assertEquals(1L, response.getId());
     assertEquals("João Atualizado", response.getNome());
     assertEquals("novo@exemplo.com", response.getEmail());

     verify(usuarioRepository).save(usuarioExistente);
     verify(usuarioMapper).updateEntityFromDto(dtoAtualizado, usuarioExistente);
     verify(usuarioMapper).toResponse(usuarioExistente);
 }

 @Test
 void naoDeveAtualizarUsuarioComEmailDuplicado() {
     Usuario usuarioExistente = Usuario.builder()
             .id(1L)
             .nome("João da Silva")
             .email("joao@exemplo.com")
             .build();

     UsuarioRequestDTO dtoAtualizado = new UsuarioRequestDTO();
     dtoAtualizado.setNome("João Qualquer");
     dtoAtualizado.setEmail("email@duplicado.com");

     when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
     when(usuarioRepository.existsByEmailIgnoreCase("email@duplicado.com")).thenReturn(true);

     RegraNegocioException ex = assertThrows(
             RegraNegocioException.class,
             () -> usuarioService.atualizar(1L, dtoAtualizado)
     );

     assertEquals("Já existe outro usuário com esse e-mail.", ex.getMessage());

     verify(usuarioMapper, never()).updateEntityFromDto(any(), any());
     verify(usuarioRepository, never()).save(any());
 }


 @Test
 void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
     UsuarioRequestDTO dtoAtualizado = new UsuarioRequestDTO();
     dtoAtualizado.setNome("Nome");
     dtoAtualizado.setEmail("email@teste.com");

     when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

     RecursoNaoEncontradoException ex = assertThrows(
             RecursoNaoEncontradoException.class,
             () -> usuarioService.atualizar(1L, dtoAtualizado)
     );

     assertEquals("Usuário não encontrado.", ex.getMessage());
     verify(usuarioRepository, never()).save(any());
     verify(usuarioMapper, never()).updateEntityFromDto(any(), any());
 }

 @Test
 void deveBuscarEntityPorIdComSucesso() {
     when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

     Usuario encontrado = usuarioService.buscarEntityPorId(1L);

     assertNotNull(encontrado);
     assertEquals(1L, encontrado.getId());
     assertEquals("João da Silva", encontrado.getNome());
     assertEquals("joao@exemplo.com", encontrado.getEmail());
 }

 @Test
 void deveLancarExcecaoAoBuscarEntityInexistente() {
     when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

     RecursoNaoEncontradoException ex = assertThrows(
             RecursoNaoEncontradoException.class,
             () -> usuarioService.buscarEntityPorId(1L)
     );

     assertEquals("Usuário não encontrado.", ex.getMessage());
 }

}
