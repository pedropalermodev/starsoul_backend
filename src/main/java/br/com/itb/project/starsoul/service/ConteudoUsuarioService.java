package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.ConteudoUsuario;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ConteudoUsuarioService {

    private final ConteudoUsuarioRepository conteudoUsuarioRepository;

    public ConteudoUsuarioService(ConteudoUsuarioRepository conteudoUsuarioRepository) {
        this.conteudoUsuarioRepository = conteudoUsuarioRepository;
    }

    public ConteudoUsuario registrarAcesso (Usuario usuario, Conteudo conteudo) {
        ConteudoUsuario conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new ConteudoUsuario(usuario, conteudo));

        conteudoUsuarioRelacao.setNumeroVisualizacoes(conteudoUsuarioRelacao.getNumeroVisualizacoes() + 1);
        conteudoUsuarioRelacao.setDataUltimoAcesso(LocalDateTime.now());

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public ConteudoUsuario favoritar(Usuario usuario, Conteudo conteudo) {
        ConteudoUsuario conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElse(new ConteudoUsuario(usuario, conteudo));

        conteudoUsuarioRelacao.setFavoritado(true);

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public ConteudoUsuario desfavoritar(Usuario usuario, Conteudo conteudo) {
        ConteudoUsuario conteudoUsuarioRelacao = conteudoUsuarioRepository
                .findByUsuarioAndConteudo(usuario, conteudo)
                .orElseThrow(() -> new NotFound("Registro não encontrado para desfavoritar"));

        conteudoUsuarioRelacao.setFavoritado(false);

        return conteudoUsuarioRepository.save(conteudoUsuarioRelacao);
    }

    public List<ConteudoUsuario> listarFavoritos(Usuario usuario) {
        return conteudoUsuarioRepository.findAllByUsuarioAndFavoritadoTrue(usuario);
    }

    public List<ConteudoUsuario> listarHistorico(Usuario usuario) {
        return conteudoUsuarioRepository.findAllByUsuario(usuario);
    }

}
