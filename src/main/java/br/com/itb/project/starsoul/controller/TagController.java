package br.com.itb.project.starsoul.controller;


import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.TagJaCadastradaException;
import br.com.itb.project.starsoul.model.Tag;
import br.com.itb.project.starsoul.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/tags")
public class TagController {

    final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<Tag> criarTag(@RequestBody Tag tag) {
        try {
            Tag novoTag = tagService.criarTag(tag);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoTag);
        } catch (TagJaCadastradaException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (BadRequest e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Tag> listarTag(@PathVariable Long id) {
        Tag tag = tagService.listarTag(id);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Tag>> listarTodasTags() {
        List<Tag> tags = tagService.listarTodosTags();
        return ResponseEntity.ok(tags);
    }

    @PutMapping("{id}")
    public ResponseEntity<Tag> atualizarTag(@PathVariable Long id, @RequestBody Tag tagAtualizado) {
        Tag tag = tagService.atualizarTag(id, tagAtualizado);
        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarTag(@PathVariable Long id) {
        tagService.deletarTag(id);
        return ResponseEntity.noContent().build();
    }

}
