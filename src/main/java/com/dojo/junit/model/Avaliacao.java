package com.dojo.junit.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity(name = "avaliacao")
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {
    private String uuid;
    private Double nota;
    private String comentario;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataEdicao;
    private Usuario usuarioAvaliado;
}
