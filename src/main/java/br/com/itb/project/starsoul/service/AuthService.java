package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.model.RedefinirSenha;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.RedefinirSenhaRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedefinirSenhaRepository redefinirSenhaRepository;
    private final JavaMailSender mailSender;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(UsuarioRepository usuarioRepository, RedefinirSenhaRepository redefinirSenhaRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.redefinirSenhaRepository = redefinirSenhaRepository;
        this.mailSender = mailSender;
    }


    public String authenticate(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getCodStatus().equalsIgnoreCase("Ativo") &&  passwordEncoder.matches(password, usuario.getSenha())) {
            return generateToken(usuario);
        }

        if (usuario.getSenha().equals(password)) {
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

        Date expiry = new Date(System.currentTimeMillis() + (15 * 60 * 1000));

        String token = generateResetToken();

        redefinirSenhaRepository.deleteByEmail(email);

        RedefinirSenha reset = new RedefinirSenha();
        reset.setEmail(email);
        reset.setToken(token);
        reset.setDataExpiracao(expiry);
        reset.setDataCriacao(new Date());

        redefinirSenhaRepository.save(reset);

        sendResetEmail(email, token);
    }


    private String generateResetToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }


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


    private void sendResetEmail(String toEmail, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(toEmail);
            helper.setSubject("Redefinição de Senha - StarSoul");
            helper.setText(buildResetEmailBody(token), true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar email de redefinição de senha", e);
        }
    }


    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        RedefinirSenha reset = redefinirSenhaRepository.findByEmailAndToken(email, token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        if (reset.getdataExpiracao().before(new Date())) {
            throw new RuntimeException("Token expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String senhaCriptografada = passwordEncoder.encode(newPassword);
        usuario.setSenha(senhaCriptografada);

        usuarioRepository.save(usuario);

        redefinirSenhaRepository.delete(reset);
    }

}
