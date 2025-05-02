package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.exceptions.TagJaCadastradaException;
import br.com.itb.project.starsoul.model.Tag;
import br.com.itb.project.starsoul.repository.TagRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final Validator validator;

    public TagService(TagRepository tagRepository, Validator validator) {
        this.tagRepository = tagRepository;
        this.validator = validator;
    }

    public Tag criarTag(Tag tag) {
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Tag> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        if (tagRepository.existsByNome(tag.getNome())) {
            throw new TagJaCadastradaException("Esta tag já está cadastrada.");
        }

        return tagRepository.save(tag);
    }

    public Tag listarTag(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new NotFound("Tag não encontrado com o id " + id));
    }

    public List<Tag> listarTodosTags() {
        return tagRepository.findAll();
    }

    @Transactional
    public Tag atualizarTag(Long id, Tag tagAtualizado) {
        Tag tagDb = tagRepository.findById(id).orElseThrow(() -> new NotFound("Tag não encontrado com o id " + id));

        Set<ConstraintViolation<Tag>> violations = validator.validate(tagAtualizado);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Tag> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        tagDb.setNome(tagAtualizado.getNome());

        return tagRepository.save(tagDb);
    }

    @Transactional
    public void deletarTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NotFound("Tag não encontrado com o id " + id);
        }
        tagRepository.deleteById(id);
    }
}
