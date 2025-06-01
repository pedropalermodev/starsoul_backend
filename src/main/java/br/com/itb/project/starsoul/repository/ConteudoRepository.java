package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.model.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    boolean existsByTitulo(String titulo);
    boolean existsByUrl(String url);

}
