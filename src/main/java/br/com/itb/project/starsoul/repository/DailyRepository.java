package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Daily;
import br.com.itb.project.starsoul.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {
    List<Daily> findByUsuario(Usuario usuario);
    Optional<Daily> findByIdAndUsuario(Long id, Usuario usuario);
}
