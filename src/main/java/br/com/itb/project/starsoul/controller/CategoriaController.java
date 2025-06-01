package br.com.itb.project.starsoul.controller;

import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Categoria categoria) {
        Categoria novoCategoria = categoriaService.criarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCategoria);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> listarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.listarCategoria(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Categoria>> listarTodasCategorias() {
        List<Categoria> categorias = categoriaService.listarTodasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoriaAtualizado) {
        Categoria categoria = categoriaService.atualizarCategoria(id, categoriaAtualizado);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }

}
