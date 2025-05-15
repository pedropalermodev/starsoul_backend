package br.com.itb.project.starsoul.repository;

import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.ConteudoUsuario;
import br.com.itb.project.starsoul.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConteudoUsuarioRepository extends JpaRepository<ConteudoUsuario, Long> {

    Optional<ConteudoUsuario> findByUsuarioAndConteudo(Usuario usuario, Conteudo conteudo);

    List<ConteudoUsuario> findAllByUsuario(Usuario usuario);

    List<ConteudoUsuario> findAllByUsuarioAndFavoritadoTrue(Usuario usuario);

}
