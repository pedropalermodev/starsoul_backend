package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.Conflict;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;


    public UsuarioService(UsuarioRepository usuarioRepository, Validator validator, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    private void validarUsuario(Usuario usuario) {
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação no usuário");
            throw new BadRequest(errorMessage);
        }
    }

    private LocalDate validarEConverterDataNascimento(String dataNascimentoStr) {
        if (dataNascimentoStr == null || dataNascimentoStr.isBlank()) {
            // Pode ser null — apenas retorna null (sem erro)
            return null;
        }

        OffsetDateTime offsetDateTime;
        try {
            offsetDateTime = OffsetDateTime.parse(dataNascimentoStr);
        } catch (DateTimeParseException e) {
            throw new BadRequest("Formato inválido para data de nascimento.");
        }

        LocalDate dataNascimento = offsetDateTime.toLocalDate();
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate dataMinima = LocalDate.of(1911, 10, 6);

        if (dataNascimento.isAfter(hoje)) {
            throw new BadRequest("Data de nascimento não pode ser no futuro.");
        }

        if (dataNascimento.isBefore(dataMinima)) {
            throw new BadRequest("Data de nascimento muito antiga.");
        }

        return dataNascimento;
    }


    public Usuario cadastrarNovoUsuarioPublico(Usuario usuario) throws BadRequest {

        usuario.setTipoConta("Usuário");
        usuario.setCodStatus("Ativo");


        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Conflict("Este e-mail já está cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        validarUsuario(usuario);

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado com o e-mail " + email));
    }

    @Transactional
    public Usuario atualizarUsuarioLogado(UserDetails userDetails, Usuario usuarioAtualizado) {
        String email = userDetails.getUsername();
        Usuario usuarioDb = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado com o e-mail " + email));

        usuarioDb.setApelido(usuarioAtualizado.getApelido());
        usuarioDb.setDataNascimento(usuarioAtualizado.getDataNascimento());
        usuarioDb.setGenero(usuarioAtualizado.getGenero());

        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().isBlank()) {
            usuarioDb.setNome(usuarioAtualizado.getNome());
        }

        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().isBlank()) {
            Optional<Usuario> usuarioComMesmoEmail = usuarioRepository.findByEmail(usuarioAtualizado.getEmail());
            if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(usuarioDb.getId())) {
                throw new Conflict("Este e-mail já está cadastrado por outro usuário.");
            }
            usuarioDb.setEmail(usuarioAtualizado.getEmail());
        }

        validarUsuario(usuarioDb);

        return usuarioRepository.save(usuarioDb);
    }

    public Usuario cadastrarUsuario(Usuario usuario, String dataNascimentoStr) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Conflict("Este e-mail já está cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        LocalDate dataNascimento = validarEConverterDataNascimento(dataNascimentoStr);
        if (dataNascimento != null) {
            usuario.setDataNascimento(dataNascimento);
        }
        validarUsuario(usuario);


        return usuarioRepository.save(usuario);
    }

    public Usuario listarUsuario(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFound("Usuário não encontrado com o id " + id));
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado, String dataNascimentoStr) {
        Usuario usuarioDb = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFound("Usuário não encontrado com o id " + id));

        usuarioDb.setNome(usuarioAtualizado.getNome());
        usuarioDb.setEmail(usuarioAtualizado.getEmail());
        usuarioDb.setCodStatus(usuarioAtualizado.getCodStatus());
        usuarioDb.setTipoConta(usuarioAtualizado.getTipoConta());
        usuarioDb.setApelido(usuarioAtualizado.getApelido());
        usuarioDb.setGenero(usuarioAtualizado.getGenero());
        usuarioDb.setDataNascimento(usuarioAtualizado.getDataNascimento());


        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(usuarioAtualizado.getSenha());
            usuarioDb.setSenha(senhaCriptografada);
        }

        Optional<Usuario> usuarioComMesmoEmail = usuarioRepository.findByEmail(usuarioAtualizado.getEmail());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(id)) {
            throw new Conflict("Este email já está cadastrado por outro usuário.");
        }

        validarUsuario(usuarioDb);

        return usuarioRepository.save(usuarioDb);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotFound("Usuário não encontrado com o id " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
