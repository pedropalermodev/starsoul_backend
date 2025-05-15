package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.ConteudoUsuario;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import br.com.itb.project.starsoul.service.ConteudoUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conteudo-usuario")
public class ConteudoUsuarioController {

    private final ConteudoUsuarioService conteudoUsuarioService;
    private final ConteudoRepository conteudoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConteudoUsuarioController(
            ConteudoUsuarioService conteudoUsuarioService,
            ConteudoRepository conteudoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.conteudoUsuarioService = conteudoUsuarioService;
        this.conteudoRepository = conteudoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Utilitário para obter o usuário autenticado a partir do e-mail (caso o principal seja o username/email)
    private Usuario getUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName(); // Padrão do Spring Security para JWT
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @PostMapping("/{conteudoId}/acessar")
    public ResponseEntity<ConteudoUsuario> registrarAcesso(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(conteudoUsuarioService.registrarAcesso(usuario, conteudo));
    }

    @PostMapping("/{conteudoId}/favoritar")
    public ResponseEntity<ConteudoUsuario> favoritar(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(conteudoUsuarioService.favoritar(usuario, conteudo));
    }

    @PostMapping("/{conteudoId}/desfavoritar")
    public ResponseEntity<ConteudoUsuario> desfavoritar(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(conteudoUsuarioService.desfavoritar(usuario, conteudo));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<ConteudoUsuario>> listarFavoritos(Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        return ResponseEntity.ok(conteudoUsuarioService.listarFavoritos(usuario));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ConteudoUsuario>> listarHistorico(Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        return ResponseEntity.ok(conteudoUsuarioService.listarHistorico(usuario));
    }
}
