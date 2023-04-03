package com.dojo.junit.response;

import com.dojo.junit.model.Avaliacao;
import com.dojo.junit.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private String nome;
    private String cpf;
    private String email;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataEdicao;
    private List<Avaliacao> avaliacoes;

    public UsuarioResponse(Usuario usuario, List<Avaliacao> avaliacoes) {
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.email = usuario.getEmail();
        this.dataCriacao = usuario.getDataCriacao();
        this.dataEdicao = usuario.getDataEdicao();
        this.avaliacoes = avaliacoes;
    }
}
