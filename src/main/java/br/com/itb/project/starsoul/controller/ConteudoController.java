package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.dto.content.ConteudoRequestDTO;
import br.com.itb.project.starsoul.dto.content.ConteudoResponseDTO;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.service.ConteudoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conteudos")
public class ConteudoController {

    final ConteudoService conteudoService;

    public ConteudoController(ConteudoService conteudoService, ConteudoRepository conteudoRepository) {
        this.conteudoService = conteudoService;
    }

    @PostMapping
    public ResponseEntity<ConteudoResponseDTO> criarConteudo(@RequestBody ConteudoRequestDTO dto) {
        Conteudo conteudoCriado = conteudoService.criarConteudo(dto);
        ConteudoResponseDTO responseDTO = conteudoService.toDto(conteudoCriado);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> listarConteudo(@PathVariable Long id) {
        Conteudo conteudo = conteudoService.listarConteudo(id);
        ConteudoResponseDTO dto = conteudoService.toDto(conteudo);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/findAll")
    public ResponseEntity<List<ConteudoResponseDTO>> listarTodosConteudos() {
        List<Conteudo> conteudos = conteudoService.listarTodosConteudos();
        List<ConteudoResponseDTO> dtos = conteudoService.toDtoList(conteudos);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> atualizarConteudo(@PathVariable Long id, @RequestBody ConteudoRequestDTO conteudoAtualizado) {
        Conteudo conteudo = conteudoService.atualizarConteudo(id, conteudoAtualizado);
        ConteudoResponseDTO dto = conteudoService.toDto(conteudo);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConteudo(@PathVariable Long id) {
        conteudoService.deletarConteudo(id);
        return ResponseEntity.noContent().build();
    }

}
