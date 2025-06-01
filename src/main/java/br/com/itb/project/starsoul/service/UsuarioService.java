package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public Usuario cadastrarNovoUsuarioPublico(Usuario usuario) throws BadRequest {

        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Usuario> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return null;
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        usuario.setTipoConta("Usuário");
        usuario.setCodStatus("Ativo");
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


        if (usuarioAtualizado.getNome() != null && !usuarioAtualizado.getNome().isBlank()) {
            usuarioDb.setNome(usuarioAtualizado.getNome());
        }

        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().isBlank()) {
            usuarioDb.setEmail(usuarioAtualizado.getEmail());
        }

        usuarioDb.setApelido(usuarioAtualizado.getApelido());
        usuarioDb.setDataNascimento(usuarioAtualizado.getDataNascimento());
        usuarioDb.setGenero(usuarioAtualizado.getGenero());


        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuarioDb);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Erros de validação ao salvar:\n");
            for (ConstraintViolation<Usuario> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        return usuarioRepository.save(usuarioDb);
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Usuario> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return null;
        }

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    public Usuario listarUsuario(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFound("Usuário não encontrado com o id " + id));
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioDb = usuarioRepository.findById(id).orElseThrow(() -> new NotFound("Usuário não encontrado com o id " + id));


        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuarioAtualizado);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Usuario> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        Optional<Usuario> usuarioComMesmoEmail = usuarioRepository.findByEmail(usuarioAtualizado.getEmail());
        if (usuarioComMesmoEmail.isPresent() && !usuarioComMesmoEmail.get().getId().equals(id)) {
            return null;
        }

        String senhaCriptografada = passwordEncoder.encode(usuarioAtualizado.getSenha());

        usuarioDb.setNome(usuarioAtualizado.getNome());
        usuarioDb.setEmail(usuarioAtualizado.getEmail());
        usuarioDb.setSenha(senhaCriptografada);
        usuarioDb.setCodStatus(usuarioAtualizado.getCodStatus());
        usuarioDb.setTipoConta(usuarioAtualizado.getTipoConta());
        usuarioDb.setApelido(usuarioAtualizado.getApelido());
        usuarioDb.setDataNascimento(usuarioAtualizado.getDataNascimento());
        usuarioDb.setGenero(usuarioAtualizado.getGenero());

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
