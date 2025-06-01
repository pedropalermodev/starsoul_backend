package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.user.UsuarioDTO;
import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrarUsuarioPublico(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.cadastrarNovoUsuarioPublico(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<Usuario> atualizarMeuPerfil( @RequestBody Usuario usuarioAtualizado, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuarioAtualizadoResponse = usuarioService.atualizarUsuarioLogado(userDetails, usuarioAtualizado);
        return ResponseEntity.ok(usuarioAtualizadoResponse);
    }

    @PostMapping("/criar/cadastro")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<Usuario> cadastrarUsuarioAdministrador(
            @RequestBody Usuario usuario,
            @RequestParam(value = "dataNascimento", required = false) String dataNascimentoStr
    ) {
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario, dataNascimentoStr);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
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
    public ResponseEntity<Usuario> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioAtualizado,
            @RequestParam(value = "dataNascimento", required = false) String dataNascimentoStr
    ) {
        Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado, dataNascimentoStr);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
