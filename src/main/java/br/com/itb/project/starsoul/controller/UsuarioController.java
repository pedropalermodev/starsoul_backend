package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.UsuarioDTO;
import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/criar/cadastro")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Usuario> cadastrarUsuarioAdministrador(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);

            if (novoUsuario == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Buscando usuário para: " + userDetails.getUsername());
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            System.out.println("Usuário encontrado: " + usuario.getEmail());
            return ResponseEntity.ok(new UsuarioDTO(usuario));
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
            throw e;
        }
    }


    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrarUsuarioPublico(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.cadastrarNovoUsuarioPublico(usuario);
            if (novoUsuario == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> listarUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.listarUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Usuario>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
