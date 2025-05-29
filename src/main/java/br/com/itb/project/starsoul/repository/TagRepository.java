package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByNome(String nome);

}
