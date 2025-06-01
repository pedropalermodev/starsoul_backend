package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.auth.TokenDTO;
import br.com.itb.project.starsoul.dto.auth.ForgotPasswordRequest;
import br.com.itb.project.starsoul.dto.auth.LoginRequest;
import br.com.itb.project.starsoul.dto.auth.ResetPasswordRequest;
import br.com.itb.project.starsoul.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.autenticar(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new TokenDTO(token));
    }
}