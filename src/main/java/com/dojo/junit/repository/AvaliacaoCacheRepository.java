package com.dojo.junit.repository;

import com.dojo.junit.model.Avaliacao;
import com.dojo.junit.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoCacheRepository extends JpaRepository<Avaliacao, String> {
    List<Avaliacao> findAllByUsuarioAvaliado(Usuario usuario);
}
