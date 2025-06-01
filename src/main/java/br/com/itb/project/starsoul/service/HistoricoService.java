package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.HistoricoRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConteudoRepository conteudoRepository;

    public HistoricoService(
        HistoricoRepository historicoRepository,
        UsuarioRepository usuarioRepository,
        ConteudoRepository conteudoRepository
    ) {
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.conteudoRepository = conteudoRepository;
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));
    }

    private Conteudo buscarConteudoPorId(Long id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Conteúdo não encontrado"));
    }

    public Historico registrarAcesso (String emailUsuario, Long conteudoId) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Conteudo conteudo = buscarConteudoPorId(conteudoId);

        Historico relacao = historicoRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new Historico(usuario, conteudo));

        relacao.setNumeroVisualizacoes(relacao.getNumeroVisualizacoes() + 1);
        relacao.setDataUltimoAcesso(LocalDateTime.now());

        return historicoRepository.save(relacao);
    }

    public Historico favoritar(String emailUsuario, Long conteudoId) {
        Usuario usuario = buscarUsuarioPorEmail(emailUsuario);
        Conteudo conteudo = buscarConteudoPorId(conteudoId);

        Historico relacao = historicoRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new Historico(usuario, conteudo));

        relacao.setFavoritado(true);

        return historicoRepository.save(relacao);
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
