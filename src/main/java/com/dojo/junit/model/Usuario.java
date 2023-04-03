package com.dojo.junit.model;

import com.dojo.junit.request.UsuarioRequest;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private String uuid;

    private String nome;

    private String cpf;

    private String email;

    private String senha;

    private OffsetDateTime dataCriacao;

    private OffsetDateTime dataEdicao;

    private Double nota;

    public Usuario(UsuarioRequest usuarioRequest, String senha) {
        this.nome = usuarioRequest.getNome();
        this.cpf = usuarioRequest.getCpf();
        this.email = usuarioRequest.getEmail();
        this.senha = senha;
        this.uuid = UUID.randomUUID().toString();
        this.dataCriacao = OffsetDateTime.now(ZoneOffset.UTC);
        this.dataEdicao = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
