package br.com.itb.project.starsoul.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


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
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("Redefinição de Senha - StarSoul");
            helper.setText(corpoEmailRedefinicao(token), true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar email de redefinição de senha", e);
        }

    }
}
