package com.dojo.junit.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity(name = "avaliacao")
public class Avaliacao {
    private String uuid;
    private Double nota;
    private String comentario;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEdicao;
    private Usuario usuarioAvaliado;
}
