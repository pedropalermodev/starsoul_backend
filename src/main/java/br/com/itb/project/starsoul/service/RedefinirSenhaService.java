package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.RedefinirSenha;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.RedefinirSenhaRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Service
public class RedefinirSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedefinirSenhaRepository redefinirSenhaRepository;
    private final EmailService emailService;

    public RedefinirSenhaService(
            UsuarioRepository usuarioRepository,
            RedefinirSenhaRepository redefinirSenhaRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.redefinirSenhaRepository = redefinirSenhaRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void esqueceuSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        Date expiry = new Date(System.currentTimeMillis() + (15 * 60 * 1000));

        String token = gerarTokenRedefinicao();

        redefinirSenhaRepository.deleteByEmail(email);

        RedefinirSenha reset = new RedefinirSenha();
        reset.setEmail(email);
        reset.setToken(token);
        reset.setDataExpiracao(expiry);
        reset.setDataCriacao(new Date());

        redefinirSenhaRepository.save(reset);

        emailService.enviarRedefinicaoSenha(email, token);
    }


    private String gerarTokenRedefinicao() {
        SecureRandom random = new SecureRandom();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }


    @Transactional
    public void redefinirSenha(String email, String token, String newPassword) {
        RedefinirSenha reset = redefinirSenhaRepository.findByEmailAndToken(email, token)
                .orElseThrow(() -> new BadRequest("Token inválido ou expirado"));

        if (reset.getDataExpiracao().before(new Date())) {
            throw new BadRequest("Token expirado");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        String senhaCriptografada = passwordEncoder.encode(newPassword);
        usuario.setSenha(senhaCriptografada);

        usuarioRepository.save(usuario);

        redefinirSenhaRepository.delete(reset);
    }
}
