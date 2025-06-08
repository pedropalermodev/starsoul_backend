package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.HistoricoRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConteudoRepository conteudoRepository;

    private HistoricoService self;

    public HistoricoService(
        HistoricoRepository historicoRepository,
        UsuarioRepository usuarioRepository,
        ConteudoRepository conteudoRepository
    ) {
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.conteudoRepository = conteudoRepository;
    }

    @Autowired
    public void setSelf(HistoricoService self) {
        this.self = self;
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));
    }

    private Conteudo buscarConteudoPorId(Long id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Conteúdo não encontrado"));
    }

    @Transactional
    public Historico registrarAcesso (String emailUsuario, Long conteudoId) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Conteudo conteudo = buscarConteudoPorId(conteudoId);

        Optional<Historico> historicoExistente = historicoRepository.findByUsuarioAndConteudo(usuario, conteudo);
        Historico relacao;

        if (historicoExistente.isPresent()) {
            relacao = historicoExistente.get();
            relacao.setNumeroVisualizacoes(relacao.getNumeroVisualizacoes() + 1);
            relacao.setDataUltimoAcesso(LocalDateTime.now());
            return historicoRepository.save(relacao);
        } else {
            relacao = new Historico(usuario, conteudo);
            relacao.setNumeroVisualizacoes(1);
            relacao.setFavoritado(false);
            relacao.setDataUltimoAcesso(LocalDateTime.now());

            try {
                return historicoRepository.save(relacao);
            } catch (DataIntegrityViolationException e) {
                return self.handleDuplicateHistoricoCreation(usuario, conteudo);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Historico handleDuplicateHistoricoCreation(Usuario usuario, Conteudo conteudo) {
        Optional<Historico> existingAgain = historicoRepository.findByUsuarioAndConteudo(usuario, conteudo);
        if (existingAgain.isPresent()) {
            Historico relacao = existingAgain.get();
            relacao.setNumeroVisualizacoes(relacao.getNumeroVisualizacoes() + 1);
            relacao.setDataUltimoAcesso(LocalDateTime.now());
            return historicoRepository.save(relacao);
        } else {
            throw new IllegalStateException("Failed to retrieve or create Historico after DataIntegrityViolationException.");
        }
    }

    @Transactional
    public Historico favoritar(String emailUsuario, Long conteudoId) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Conteudo conteudo = buscarConteudoPorId(conteudoId);

        Optional<Historico> historicoExistente = historicoRepository.findByUsuarioAndConteudo(usuario, conteudo);
        Historico relacao;

        if (historicoExistente.isPresent()) {
            relacao = historicoExistente.get();
        } else {
            relacao = new Historico(usuario, conteudo);
            relacao.setNumeroVisualizacoes(0);
            relacao.setDataUltimoAcesso(LocalDateTime.now());
        }
        relacao.setFavoritado(true);

        try {
            return historicoRepository.save(relacao);
        } catch (DataIntegrityViolationException e) {
            return self.handleDuplicateHistoricoFavoritar(usuario, conteudo);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Historico handleDuplicateHistoricoFavoritar(Usuario usuario, Conteudo conteudo) {
        Optional<Historico> existingAgain = historicoRepository.findByUsuarioAndConteudo(usuario, conteudo);
        if (existingAgain.isPresent()) {
            Historico relacao = existingAgain.get();
            relacao.setFavoritado(true);
            return historicoRepository.save(relacao);
        } else {
            throw new IllegalStateException("Failed to retrieve or favorite Historico after DataIntegrityViolationException.");
        }
    }

    public Historico desfavoritar(String emailUsuario, Long conteudoId) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Conteudo conteudo = buscarConteudoPorId(conteudoId);

        Historico relacao = historicoRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElseThrow(() -> new NotFound("Registro não encontrado para desfavoritar"));

        relacao.setFavoritado(false);

        return historicoRepository.save(relacao);
    }

    public List<Historico> listarFavoritos(String emailUsuario) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        return historicoRepository.findAllByUsuarioAndFavoritadoTrue(usuario);
    }

    public List<Historico> listarHistorico(String emailUsuario) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        return historicoRepository.findAllByUsuario(usuario);
    }

}
