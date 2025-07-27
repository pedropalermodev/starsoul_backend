package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Daily;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.DailyRepository;
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
    private final TextEncryptor encryptor;
    private final Validator validator;

    public DailyService(
            DailyRepository dailyRepository,
            Validator validator,
            @Value("${app.crypto.password}") String password,
            @Value("${app.crypto.salt}") String salt)
    {
        this.dailyRepository = dailyRepository;
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

    public Daily cadastrarAnotacao(Daily anotacao) {
        validarAnotacao(anotacao);
        anotacao.setAnotacao(encryptor.encrypt(anotacao.getAnotacao()));
        return dailyRepository.save(anotacao);
    }

    public Daily listarAnotacao(Long id) {
        Daily a = dailyRepository.findById(id).orElseThrow(() -> new NotFound("Anotação não encontrado com o id " + id));
        a.setAnotacao(encryptor.decrypt(a.getAnotacao()));
        return a;
    }

    public List<Daily> listarTodasAnotacoes() {
        List<Daily> lista = dailyRepository.findAll();
        lista.forEach(a -> a.setAnotacao(encryptor.decrypt(a.getAnotacao())));
        return lista;
    }

    public List<Daily> listarPorUsuario(Long usuarioId) {
        List<Daily> lista = dailyRepository.findByUsuarioId(usuarioId);
        lista.forEach(a -> a.setAnotacao(encryptor.decrypt(a.getAnotacao())));
        return lista;
    }


    @Transactional
    public void deletarAnotacao(Long id) {
        if (!dailyRepository.existsById(id)) {
            throw new NotFound("Anotação não encontrado com o id " + id);
        }
        dailyRepository.deleteById(id);
    }
}
