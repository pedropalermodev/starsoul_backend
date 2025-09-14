package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    Optional<Historico> findByUsuarioAndConteudo(Usuario usuario, Conteudo conteudo);

    List<Historico> findAllByUsuario(Usuario usuario);

    List<Historico> findAllByUsuarioAndFavoritadoTrue(Usuario usuario);

    void deleteAllByUsuario(Usuario usuario);

}
