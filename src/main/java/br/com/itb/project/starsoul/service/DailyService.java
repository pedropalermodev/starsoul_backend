package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Daily;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.DailyRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DailyService {

    private final DailyRepository dailyRepository;
    private final UsuarioRepository usuarioRepository;
    private final TextEncryptor encryptor;
    private final Validator validator;

    public DailyService(
            DailyRepository dailyRepository,
            UsuarioRepository usuarioRepository,
            Validator validator,
            @Value("${app.crypto.password}") String password,
            @Value("${app.crypto.salt}") String salt)
    {
        this.dailyRepository = dailyRepository;
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
        this.encryptor = Encryptors.text(password, salt);
    }

    private void validarAnotacao(Daily anotacao) {
        Set<ConstraintViolation<Daily>> violations = validator.validate(anotacao);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação na anotação");
            throw new BadRequest(errorMessage);
        }
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));
    }

    public Daily cadastrarAnotacao(Daily anotacao, String emailUsuario) {
        validarAnotacao(anotacao);
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        anotacao.setUsuario(usuario);
        anotacao.setHumor(encryptor.encrypt(anotacao.getHumor()));
        anotacao.setAnotacao(encryptor.encrypt(anotacao.getAnotacao()));
        return dailyRepository.save(anotacao);
    }

    public Daily listarAnotacao(Long id, String emailUsuario) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Daily a = dailyRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new NotFound("Anotação não encontrada ou não pertence ao usuário"));
        a.setHumor(encryptor.decrypt(a.getHumor()));
        a.setAnotacao(encryptor.decrypt(a.getAnotacao()));
        return a;
    }


    public List<Daily> listarPorUsuarioEmail(String emailUsuario) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        List<Daily> lista = dailyRepository.findByUsuario(usuario);
        lista.forEach(a -> a.setHumor(encryptor.decrypt(a.getHumor())));
        lista.forEach(a -> a.setAnotacao(encryptor.decrypt(a.getAnotacao())));
        return lista;
    }


    @Transactional
    public void deletarAnotacao(Long id, String emailUsuario) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Daily anotacao = dailyRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new NotFound("Anotação não encontrada ou não pertence ao usuário"));
        dailyRepository.delete(anotacao);
    }

}
