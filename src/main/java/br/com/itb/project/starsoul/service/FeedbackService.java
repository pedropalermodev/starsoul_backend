package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.model.Feedback;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.FeedbackRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final Validator validator;

    public FeedbackService(FeedbackRepository feedbackRepository, Validator validator) {
        this.feedbackRepository = feedbackRepository;
        this.validator = validator;
    }

    public Feedback criarFeedback(Feedback feedback) {
        Set<ConstraintViolation<Feedback>> violations = validator.validate(feedback);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação no feedback");
            throw new BadRequest(errorMessage);
        }

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> listarTodosFeedbacks() {
        return feedbackRepository.findAll();
    }


}
