package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.model.Feedback;
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
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Feedback> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        return feedbackRepository.save(feedback);
    }

    public List<Feedback> listarTodosFeedbacks() {
        return feedbackRepository.findAll();
    }


}
