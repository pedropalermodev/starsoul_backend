package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyRepository extends JpaRepository<Daily, Long> {
    List<Daily> findByUsuarioId(Long usuarioId);
}
