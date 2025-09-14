package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.Historico;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import br.com.itb.project.starsoul.service.HistoricoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conteudo-usuario")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @PostMapping("/{conteudoId}/acessar")
    public ResponseEntity<Historico> registrarAcesso(@PathVariable Long conteudoId, Authentication authentication) {
        try {
            Historico historico = historicoService.registrarAcesso(authentication.getName(), conteudoId);
            return ResponseEntity.ok(historico);
        } catch (UnexpectedRollbackException ex) {
            System.out.println("DEBUG: UnexpectedRollbackException capturada no Controller, mas operação provavelmente bem-sucedida devido ao retry.");
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/{conteudoId}/favoritar")
    public ResponseEntity<Historico> favoritar(@PathVariable Long conteudoId, Authentication authentication) {
        try {
            Historico historico = historicoService.favoritar(authentication.getName(), conteudoId);
            return ResponseEntity.ok(historico);
        } catch (UnexpectedRollbackException ex) {
            System.out.println("DEBUG: UnexpectedRollbackException capturada no Controller para favoritar.");
            return ResponseEntity.ok(null); // Ou um objeto Historico vazio/com mensagem
        }
    }

    @PostMapping("/{conteudoId}/desfavoritar")
    public ResponseEntity<Historico> desfavoritar(@PathVariable Long conteudoId, Authentication authentication) {
        return ResponseEntity.ok(historicoService.desfavoritar(authentication.getName(), conteudoId));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<Historico>> listarFavoritos(Authentication authentication) {
        return ResponseEntity.ok(historicoService.listarFavoritos(authentication.getName()));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<Historico>> listarHistorico(Authentication authentication) {
        return ResponseEntity.ok(historicoService.listarHistorico(authentication.getName()));
    }

    @DeleteMapping("/historico")
    public ResponseEntity<Void> apagarHistorico(Authentication authentication) {
        historicoService.apagarHistorico(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
