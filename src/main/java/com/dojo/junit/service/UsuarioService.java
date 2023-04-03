package com.dojo.junit.service;

import com.dojo.junit.exception.DuplicatedUserException;
import com.dojo.junit.exception.NotFoundException;
import com.dojo.junit.mensageria.KafkaService;
import com.dojo.junit.repository.AvaliacaoRepository;
import com.dojo.junit.request.EmailRequest;
import com.dojo.junit.request.UsuarioRequest;
import com.dojo.junit.model.Usuario;
import com.dojo.junit.repository.AvaliacaoCacheRepository;
import com.dojo.junit.repository.UsuarioRepository;
import com.dojo.junit.response.UsuarioResponse;
import com.dojo.junit.security.PasswordEnconder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private AvaliacaoCacheRepository avaliacaoCacheRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private PasswordEnconder passwordEnconder;

    public Usuario createUser(UsuarioRequest usuarioRequest) throws Exception {
        if(usuarioRepository.findByEmail(usuarioRequest.getEmail()).isPresent())
            throw new DuplicatedUserException(usuarioRequest.getEmail());

        var senha = passwordEnconder.encriptarSenha(usuarioRequest.getSenha());

        var usuario = usuarioRepository.save(new Usuario(usuarioRequest, senha));

        kafkaService.publicarNaFila(usuario);

        return usuario;
    }

    public UsuarioResponse obterUsuario(String uuid) throws Exception {
        var usuarioOptional = usuarioRepository.findById(uuid);

        if (usuarioOptional.isEmpty())
            throw new NotFoundException("User "+uuid+" not found!");

        var usuario = usuarioOptional.get();

        var avaliacaoes = avaliacaoCacheRepository.findAllByUsuarioAvaliado(usuario);

        if (avaliacaoes.isEmpty()) {
            avaliacaoes = avaliacaoRepository.findAllByUsuarioAvaliado(usuario);
        }

        return new UsuarioResponse(usuario, avaliacaoes);
    }

    public void enviarEmail(String mensagem, String uuidUsuario) throws Exception {
        var usuarioOptional = usuarioRepository.findById(uuidUsuario);

        if (usuarioOptional.isEmpty())
            throw new NotFoundException("User "+uuidUsuario+" not found!");

        EmailRequest emailRequest = new EmailRequest(
                usuarioOptional.get().getEmail(),
                "Email automático",
                mensagem
        );

        emailService.enviarEmail(emailRequest);
    }

}