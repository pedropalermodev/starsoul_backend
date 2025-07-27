package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.user.DailyDTO;
import br.com.itb.project.starsoul.model.Daily;
import br.com.itb.project.starsoul.service.DailyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anotacoes")
public class DailyController {

    private final DailyService dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Daily> cadastrarAnotacao(@RequestBody @Valid DailyDTO requestDTO, Authentication authentication) {
        String email = authentication.getName();

        Daily novaAnotacao = dailyService.cadastrarAnotacao(
                requestDTO.getHumor(),
                requestDTO.getAnotacao(),
                requestDTO.getUsuarioId(), // Passa o ID do usuário
                email // E o email do usuário logado para segurança/validação no serviço
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(novaAnotacao);
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<Daily> listarMinhaAnotacao(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Daily anotacao = dailyService.listarAnotacao(id, email);
        return ResponseEntity.ok(anotacao);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Daily>> listarMinhasAnotacoes(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dailyService.listarPorUsuarioEmail(email));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarAnotacao(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        dailyService.deletarAnotacao(id, email);
        return ResponseEntity.noContent().build();
    }


}
