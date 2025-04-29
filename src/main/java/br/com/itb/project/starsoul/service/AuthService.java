package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.model.PasswordReset;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.PasswordResetRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordResetRepository passwordResetRepository;
    private final JavaMailSender mailSender;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(UsuarioRepository usuarioRepository, PasswordResetRepository passwordResetRepository, JavaMailSender mailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.mailSender = mailSender;
    }

    public String authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getSenhaHash().equals(password)) {
            return generateToken(usuario);
        }

        return null;
    }

    public String generateToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("role", usuario.getTipoConta())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .sign(algorithm);
    }



    @Transactional
    public void forgotPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Date expiry = new Date(System.currentTimeMillis() + (15 * 60 * 1000)); // 15 minutos

        String token = generateResetToken(); // Aqui gera o token

        passwordResetRepository.deleteByEmail(email); // remove tokens antigos

        PasswordReset reset = new PasswordReset();
        reset.setEmail(email);
        reset.setToken(token);
        reset.setTokenExpiry(expiry);
        reset.setCreatedAt(new Date());

        passwordResetRepository.save(reset);

        // Enviar o e-mail bonitinho
        sendResetEmail(email, token);
    }

    // Método para gerar token de 6 dígitos
    private String generateResetToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // Gera entre 100000 e 999999
        return String.valueOf(token);
    }

    // Método para construir o HTML do e-mail
    private String buildResetEmailBody(String token) {
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

    // Método para enviar o e-mail (usando JavaMailSender)
    private void sendResetEmail(String toEmail, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("Redefinição de Senha - StarSoul");
            helper.setText(buildResetEmailBody(token), true); // true = envia como HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar email de redefinição de senha", e);
        }
    }


    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        PasswordReset reset = passwordResetRepository.findByEmailAndToken(email, token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        if (reset.getTokenExpiry().before(new Date())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setSenhaHash(newPassword);
        usuarioRepository.save(usuario);

        passwordResetRepository.delete(reset); // remove o token após resetar
    }

}
