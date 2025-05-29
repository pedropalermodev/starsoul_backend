package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByIdIn(List<Long> ids);

}
