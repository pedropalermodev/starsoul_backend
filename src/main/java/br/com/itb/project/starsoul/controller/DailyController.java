package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.model.Daily;
import br.com.itb.project.starsoul.service.DailyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anotacoes")
public class DailyController {

    private final DailyService dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Daily> cadastrarAnotacao(@RequestBody Daily anotacao) {
        Daily novaAnotacao = dailyService.cadastrarAnotacao(anotacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAnotacao);
    }

    @GetMapping("{id}")
    public ResponseEntity<Daily> listarAnotacao(@PathVariable Long id) {
        Daily anotacao = dailyService.listarAnotacao(id);
        return ResponseEntity.ok(anotacao);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Daily>> listarTodasAnotacoes() {
        List<Daily> anotacoes = dailyService.listarTodasAnotacoes();
        return ResponseEntity.ok(anotacoes);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Daily>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(dailyService.listarPorUsuario(usuarioId));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarAnotacao(@PathVariable Long id) {
        dailyService.deletarAnotacao(id);
        return ResponseEntity.noContent().build();
    }

}
