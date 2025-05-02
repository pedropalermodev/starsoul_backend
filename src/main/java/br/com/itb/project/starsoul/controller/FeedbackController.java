package br.com.itb.project.starsoul.controller;


import br.com.itb.project.starsoul.model.Feedback;
import br.com.itb.project.starsoul.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<Feedback> criarFeedback(@RequestBody Feedback feedback) {
        Feedback novoFeedback = feedbackService.criarFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFeedback);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Feedback>> listarTodasFeedbacks() {
        List<Feedback> feedbacks = feedbackService.listarTodosFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

}
