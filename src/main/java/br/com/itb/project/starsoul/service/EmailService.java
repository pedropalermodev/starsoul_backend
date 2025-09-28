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
        return "<!DOCTYPE html>" +
                "<html lang='pt-BR'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Redefinição de Senha</title>" +
                "    <style>" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f0ffef; margin:0; padding:0; }" +
                "        .container { max-width:500px; box-shadow:0 4px 12px rgba(0,0,0,0.08); padding:30px; }" +
                "        .header-logo { text-align:center; padding-bottom:25px; border-bottom:1px solid #e0eeef; margin-bottom:20px; }" +
                "        .header-logo img { max-width:180px; height:auto; display:block; margin:0 auto; }" +
                "        h2 { color:#1a4a6b; font-size:20px; margin-bottom:20px; }" +
                "        p { color:#4a4a4a; line-height:1.6; margin-bottom:15px; text-align:justify; }" +
                "        .highlight { color:#004d66; font-weight:600; font-style:italic; }" +
                "        .token-block { text-align:center; margin:25px 0; padding:20px; background:#1a4a6b; border-radius:8px; font-family:monospace, Arial, sans-serif; font-size:28px; font-weight:bold; color:#ffffff; box-shadow: 4px 4px 9px 1px rgba(26,74,107,0.75); -webkit-box-shadow: 4px 4px 9px 1px rgba(26,74,107,0.75); -moz-box-shadow: 4px 4px 9px 1px rgba(26,74,107,0.75);}" +
                "        .footer { color:#888888; font-size:12px; margin-top:30px; text-align:center; padding-top:15px; border-top:1px solid #f0f0f0; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header-logo'>" +
                "            <img src='https://starsoul.netlify.app/assets/starsoul-lettermark-blue-U4YngB9y.png' alt='Logo StarSoul' />" +
                "        </div>" +
                "        <h2>Solicitação de Redefinição de Senha</h2>" +
                "        <p>Prezado(a) usuário(a),</p>" +
                "        <p>Recebemos uma solicitação para <span class='highlight'>redefinir a senha</span> de sua conta. Para garantir a segurança de seus dados e concluir este processo, utilize o <span class='highlight'>Código de Verificação</span> exclusivo apresentado abaixo.</p>" +
                "        <div class='token-block'>" + token + "</div>" +
                "        <p><span class='highlight'>Atenção:</span> Por motivos de segurança, este código expira em <span class='highlight'>15 minutos</span>. Por favor, utilize-o imediatamente na tela de redefinição de senha.</p>" +
                "        <p>Se você <span class='highlight'>NÃO</span> solicitou esta alteração, pedimos que ignore este e-mail. Nenhuma ação será tomada em sua conta.</p>" +
                "        <div class='footer'>Atenciosamente,<br>Equipe de Suporte StarSoul</div>" +
                "    </div>" +
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
