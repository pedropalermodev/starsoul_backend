package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.ECPrivateKey; // Importe ECPrivateKey
import java.security.KeyFactory; // Importe KeyFactory
import java.security.spec.PKCS8EncodedKeySpec; // Importe PKCS8EncodedKeySpec
import java.util.Base64; // Importe Base64
import java.security.GeneralSecurityException; // Importe GeneralSecurityException para tratamento de erros

import java.util.Date;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.es256.private-key}")
    private String privateKeyPem;

    @Value("${jwt.es256.key-id}")
    private String keyId;

    @Value("${jwt.expiration}")
    private long expiration;

    private Algorithm algorithm;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @jakarta.annotation.PostConstruct
    public void init() throws GeneralSecurityException {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyPem);

            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);

            this.algorithm = Algorithm.ECDSA256(null, privateKey); // CORRIGIDO
        } catch (Exception e) {
            throw new GeneralSecurityException("Erro ao inicializar chave privada ES256", e);
        }
    }


    public String autenticar(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null || !usuario.getCodStatus().equalsIgnoreCase("Ativo") || !passwordEncoder.matches(password, usuario.getSenha())) {
            throw new BadRequest("Email ou senha inválidos");
        }

        return gerarToken(usuario);
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withKeyId(keyId)
                .withSubject(usuario.getEmail())
                .withClaim("role", usuario.getTipoConta())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .sign(algorithm);
    }
}