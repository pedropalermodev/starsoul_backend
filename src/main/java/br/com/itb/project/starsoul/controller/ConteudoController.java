package br.com.itb.project.starsoul.controller;


import br.com.itb.project.starsoul.dto.ConteudoRequestDTO;
import br.com.itb.project.starsoul.dto.ConteudoResponseDTO;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.service.ConteudoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/conteudos")
public class ConteudoController {

    final ConteudoService conteudoService;
    private final ConteudoRepository conteudoRepository;

    public ConteudoController(ConteudoService conteudoService, ConteudoRepository conteudoRepository) {
        this.conteudoService = conteudoService;
        this.conteudoRepository = conteudoRepository;
    }

    @PostMapping
    public ResponseEntity<ConteudoResponseDTO> criarConteudo(@RequestBody ConteudoRequestDTO dto) {
        Conteudo conteudoCriado = conteudoService.criarConteudo(dto);
        ConteudoResponseDTO responseDTO = conteudoService.toDto(conteudoCriado);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> getConteudo(@PathVariable Long id) {
        Conteudo conteudo = conteudoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Conteúdo não encontrado"));

        ConteudoResponseDTO dto = conteudoService.toDto(conteudo);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/findAll")
    public ResponseEntity<List<ConteudoResponseDTO>> listarTodosConteudos() {
        List<Conteudo> conteudos = conteudoService.listarTodosConteudos();
        List<ConteudoResponseDTO> dtos = conteudoService.toDtoList(conteudos);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("{id}")
    public ResponseEntity<Conteudo> atualizarConteudo(@PathVariable Long id, @RequestBody ConteudoRequestDTO conteudoAtualizado) {
        Conteudo conteudo = conteudoService.atualizarConteudo(id, conteudoAtualizado);
        return ResponseEntity.ok(conteudo);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarConteudo(@PathVariable Long id) {
        conteudoService.deletarConteudo(id);
        return ResponseEntity.noContent().build();
    }

}
