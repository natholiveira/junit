package com.dojo.junit.service;

import com.dojo.junit.exception.DuplicatedUserException;
import com.dojo.junit.exception.NotFoundException;
import com.dojo.junit.mensageria.KafkaService;
import com.dojo.junit.model.Avaliacao;
import com.dojo.junit.model.Usuario;
import com.dojo.junit.repository.AvaliacaoCacheRepository;
import com.dojo.junit.repository.AvaliacaoRepository;
import com.dojo.junit.repository.UsuarioRepository;
import com.dojo.junit.request.EmailRequest;
import com.dojo.junit.request.UsuarioRequest;
import com.dojo.junit.response.UsuarioResponse;
import com.dojo.junit.security.PasswordEnconder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private AvaliacaoCacheRepository avaliacaoCacheRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private PasswordEnconder passwordEnconder;

    @InjectMocks
    private UsuarioService service;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;

    @Test
    public void criarUsuarioComSucesso() throws Exception {

        UsuarioRequest usuarioRequest = new UsuarioRequest(
          "ana",
          "1234124214",
          "ana@email.com",
          "1234"
        );

        var senhaEncriptografada = "uah@w54";

        Usuario usuarioExpected = new Usuario(usuarioRequest, senhaEncriptografada);

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEnconder.encriptarSenha(any())).thenReturn(senhaEncriptografada);
        when(usuarioRepository.save(any())).thenReturn(usuarioExpected);
        doNothing().when(kafkaService).publicarNaFila(any());

        var usuario = service.createUser(usuarioRequest);

        Assertions.assertNotNull(usuario);
        Assertions.assertEquals(usuarioExpected, usuario);
    }

    @Test
    public void lancarExcecaoQuandoUsuarioDuplicado() {
        UsuarioRequest usuarioRequest = new UsuarioRequest(
                "ana",
                "1234124214",
                "ana@email.com",
                "1234"
        );

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(new Usuario()));

        Assertions.assertThrows(DuplicatedUserException.class, () -> service.createUser(usuarioRequest));

        verify(passwordEnconder, times(0)).encriptarSenha(any());
        verify(usuarioRepository, times(0)).save(any());
        verify(kafkaService, (times(0))).publicarNaFila(any());
    }

    @Test
    public void obterUsuarioEmCacheComSucesso() throws Exception {
        Usuario usuario = new Usuario(
                "id",
                "ana",
                "1234124214",
                "ana@email.com",
                "1234"  ,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                3.0
        );

        Avaliacao avaliacao = new Avaliacao(
                "id",
                4.0,
                "bom",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                usuario
        );

        UsuarioResponse usuarioExpected = new UsuarioResponse(usuario, Arrays.asList(avaliacao));

        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));
        when(avaliacaoCacheRepository.findAllByUsuarioAvaliado(any())).thenReturn(Arrays.asList(avaliacao));

        var usuarioResponse = service.obterUsuario("id");

        Assertions.assertNotNull(usuarioResponse);
        Assertions.assertEquals(usuarioExpected, usuarioResponse);

        verify(avaliacaoRepository, times(0)).findAllByUsuarioAvaliado(any());
    }

    @Test
    public void obterUsuarioEmBancoComSucesso() throws Exception {
        Usuario usuario = new Usuario(
                "id",
                "ana",
                "1234124214",
                "ana@email.com",
                "1234"  ,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                3.0
        );

        Avaliacao avaliacao = new Avaliacao(
                "id",
                4.0,
                "bom",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                usuario
        );

        UsuarioResponse usuarioExpected = new UsuarioResponse(usuario, Arrays.asList(avaliacao));

        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));
        when(avaliacaoCacheRepository.findAllByUsuarioAvaliado(any())).thenReturn(Arrays.asList());
        when(avaliacaoRepository.findAllByUsuarioAvaliado(any())).thenReturn(Arrays.asList(avaliacao));

        var usuarioResponse = service.obterUsuario("id");

        Assertions.assertNotNull(usuarioResponse);
        Assertions.assertEquals(usuarioExpected, usuarioResponse);
    }

    @Test
    public void lancarExcecaoQuandoNaoEncontrarUsuario() {
        when(usuarioRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> service.obterUsuario("id"));
        verify(avaliacaoRepository, times(0)).findAllByUsuarioAvaliado(any());
        verify(avaliacaoCacheRepository, times(0)).findAllByUsuarioAvaliado(any());
    }

    @Test
    public void enviarEmailComSucesso() throws Exception {
        Usuario usuario = new Usuario(
                "id",
                "ana",
                "1234124214",
                "ana@email.com",
                "1234"  ,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                3.0
        );

        String mensagem = "Olá, usuario";

        EmailRequest emailRequestExpeted = new EmailRequest(
                usuario.getEmail(),
                "Email automático",
                mensagem
        );

        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));

        service.enviarEmail(mensagem, "id");

        verify(emailService).enviarEmail(emailRequestArgumentCaptor.capture());

        Assertions.assertEquals(emailRequestExpeted, emailRequestArgumentCaptor.getValue());
    }

    @Test
    public void lancarExcecaoQuandoNaoEncontrarUsuarioNoEnvioDeEmail() {
        when(usuarioRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> service.enviarEmail("mensagem", "id"));
        verify(emailService, times(0)).enviarEmail(any());
    }

    @Test
    public void enviarEmailsNotaBaixa() {
        Usuario usuario = new Usuario(
                "id",
                "ana",
                "1234124214",
                "ana@email.com",
                "1234"  ,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                3.0
        );

        Usuario usuario2 = new Usuario(
                "id",
                "beatriz",
                "1234124214",
                "beatriz@email.com",
                "1234"  ,
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC),
                3.0
        );

        when(usuarioRepository.findAllByNota(any())).thenReturn(Arrays.asList(usuario, usuario2));

        service.enviarEmailsUsuariosNotasBaixas(3.0);

        verify(emailService, times(2)).enviarEmail(any());
    }
}
