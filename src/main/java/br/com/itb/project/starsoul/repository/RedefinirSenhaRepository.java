package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.RedefinirSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedefinirSenhaRepository extends JpaRepository<RedefinirSenha, Long> {

    Optional<RedefinirSenha> findByEmailAndToken(String email, String token);
    void deleteByEmail(String email);
}
