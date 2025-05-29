package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> { }
