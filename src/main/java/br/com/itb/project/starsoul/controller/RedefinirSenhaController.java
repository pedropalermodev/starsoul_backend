package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.auth.ForgotPasswordRequest;
import br.com.itb.project.starsoul.dto.auth.ResetPasswordRequest;
import br.com.itb.project.starsoul.service.RedefinirSenhaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
public class RedefinirSenhaController {

    private final RedefinirSenhaService redefinirSenhaService;

    public RedefinirSenhaController(RedefinirSenhaService redefinirSenhaService) {
        this.redefinirSenhaService = redefinirSenhaService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> esqueceuSenha(@Valid @RequestBody ForgotPasswordRequest request) {
        redefinirSenhaService.esqueceuSenha(request.getEmail());
        return ResponseEntity.ok("Token enviado para o email!");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> redefinirSenha(@Valid @RequestBody ResetPasswordRequest request) {
        redefinirSenhaService.redefinirSenha(request.getEmail(), request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }

}
