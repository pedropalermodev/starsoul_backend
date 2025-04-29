package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    Optional<PasswordReset> findByEmailAndToken(String email, String token);
    void deleteByEmail(String email);
}
