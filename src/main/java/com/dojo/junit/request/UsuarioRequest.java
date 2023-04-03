package com.dojo.junit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    private String nome;

    private String cpf;

    private String email;

    private String senha;
}
