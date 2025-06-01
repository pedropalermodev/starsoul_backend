package br.com.itb.project.starsoul.dto.auth;

import jakarta.validation.constraints.*;

public class ResetPasswordRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String token;

    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String newPassword;

    // Getters and Setters

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
