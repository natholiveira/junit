package com.dojo.junit.service;

import com.dojo.junit.mensageria.KafkaService;
import com.dojo.junit.model.Usuario;
import com.dojo.junit.repository.AvaliacaoCacheRepository;
import com.dojo.junit.repository.AvaliacaoRepository;
import com.dojo.junit.repository.UsuarioRepository;
import com.dojo.junit.request.UsuarioRequest;
import com.dojo.junit.security.PasswordEnconder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    @Test
    public void criarUsuarioComSucesso() throws Exception {
        UsuarioRequest usuarioRequest = new UsuarioRequest(
               "ana",
                "1231242124",
                "ana@email.com",
                "1234"
        );

        var senhaEncriptada = "abc123d";
        Usuario usuarioExpected = new Usuario(
                usuarioRequest,
                senhaEncriptada
        );

        when(passwordEnconder.encriptarSenha(anyString())).thenReturn(senhaEncriptada);
        when(usuarioRepository.save(any())).thenReturn(usuarioExpected);
        doNothing().when(kafkaService).publicarNaFila(any());

        var usuario = service.createUser(usuarioRequest);

        Assertions.assertNotNull(usuario);
        Assertions.assertEquals(usuarioExpected, usuario);
    }
}
