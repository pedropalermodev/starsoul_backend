package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import br.com.itb.project.starsoul.service.HistoricoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/conteudo-usuario")
public class HistoricoController {

    private final HistoricoService historicoService;
    private final ConteudoRepository conteudoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoController(
            HistoricoService historicoService,
            ConteudoRepository conteudoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.historicoService = historicoService;
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
    public ResponseEntity<Historico> registrarAcesso(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(historicoService.registrarAcesso(usuario, conteudo));
    }

    @PostMapping("/{conteudoId}/favoritar")
    public ResponseEntity<Historico> favoritar(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(historicoService.favoritar(usuario, conteudo));
    }

    @PostMapping("/{conteudoId}/desfavoritar")
    public ResponseEntity<Historico> desfavoritar(@PathVariable Long conteudoId, Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        Conteudo conteudo = conteudoRepository.findById(conteudoId)
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado"));
        return ResponseEntity.ok(historicoService.desfavoritar(usuario, conteudo));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<Historico>> listarFavoritos(Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        return ResponseEntity.ok(historicoService.listarFavoritos(usuario));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<Historico>> listarHistorico(Authentication authentication) {
        Usuario usuario = getUsuarioAutenticado(authentication);
        return ResponseEntity.ok(historicoService.listarHistorico(usuario));
    }
}
