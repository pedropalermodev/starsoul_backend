package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.model.ConteudoCategoria;
import br.com.itb.project.starsoul.model.ConteudoCategoriaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConteudoCategoriaRepository extends JpaRepository<ConteudoCategoria, ConteudoCategoriaId> {
    List<ConteudoCategoria> findByCategoria(Categoria categoria);
}
