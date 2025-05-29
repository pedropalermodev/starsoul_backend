package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.HistoricoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoService {

    private final HistoricoRepository conteudoUsuarioRepository;

    public HistoricoService(HistoricoRepository conteudoUsuarioRepository) {
        this.conteudoUsuarioRepository = conteudoUsuarioRepository;
    }

    public Historico registrarAcesso (Usuario usuario, Conteudo conteudo) {
        Historico conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new Historico(usuario, conteudo));

        conteudoUsuarioRelacao.setNumeroVisualizacoes(conteudoUsuarioRelacao.getNumeroVisualizacoes() + 1);
        conteudoUsuarioRelacao.setDataUltimoAcesso(LocalDateTime.now());

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public Historico favoritar(Usuario usuario, Conteudo conteudo) {
        Historico conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new Historico(usuario, conteudo));

        conteudoUsuarioRelacao.setFavoritado(true);

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public Historico desfavoritar(Usuario usuario, Conteudo conteudo) {
        Historico conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElseThrow(() -> new NotFound("Registro não encontrado para desfavoritar"));

        conteudoUsuarioRelacao.setFavoritado(false);

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public List<Historico> listarFavoritos(Usuario usuario) {
        return conteudoUsuarioRepository.findAllByUsuarioAndFavoritadoTrue(usuario);
    }

    public List<Historico> listarHistorico(Usuario usuario) {
        return conteudoUsuarioRepository.findAllByUsuario(usuario);
    }

}
