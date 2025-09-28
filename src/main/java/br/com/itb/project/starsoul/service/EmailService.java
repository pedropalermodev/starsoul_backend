package br.com.itb.project.starsoul.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    private final String apiKey = System.getenv("BREVO_API_KEY");
    private final String fromEmail = System.getenv("BREVO_FROM_EMAIL");
    private final String fromName = System.getenv("BREVO_FROM_NAME");

    private String corpoEmailRedefinicao(String token) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Redefinição de Senha</h2>" +
                "<p>Olá,</p>" +
                "<p>Você solicitou a redefinição de sua senha.</p>" +
                "<p><strong>Seu código de verificação é:</strong></p>" +
                "<h1 style='color: #455A64;'>" + token + "</h1>" +
                "<p>Este código expira em 15 minutos.</p>" +
                "<p>Se você não solicitou esta alteração, ignore este email.</p>" +
                "<br>" +
                "<p>Equipe StarSoul 🚀 </p>" +
                "</body>" +
                "</html>";
    }

    public void enviarRedefinicaoSenha(String toEmail, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            Map<String, Object> body = Map.of(
                    "sender", Map.of("email", fromEmail, "name", fromName),
                    "to", new Map[]{ Map.of("email", toEmail) },
                    "subject", "Redefinição de Senha - StarSoul",
                    "htmlContent", corpoEmailRedefinicao(token)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erro ao enviar e-mail via Brevo: " + response.getBody());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar email de redefinição de senha", e);
        }
    }
}