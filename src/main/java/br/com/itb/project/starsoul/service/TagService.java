package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.Conflict;
import br.com.itb.project.starsoul.exceptions.NotFound;
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

    private void validarTag(Tag tag) {
        Set<ConstraintViolation<Tag>> violations = validator.validate(tag);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação na tag");
            throw new BadRequest(errorMessage);
        }
    }

    public Tag criarTag(Tag tag) {

        validarTag(tag);

        if (tagRepository.existsByNome(tag.getNome())) {
            throw new Conflict("Esta tag já está cadastrada.");
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

        tagDb.setNome(tagAtualizado.getNome());

        if (!tagDb.getNome().equals(tagAtualizado.getNome()) &&
                tagRepository.existsByNome(tagAtualizado.getNome())) {
            throw new Conflict("Esta tag já está cadastrada.");
        }

        validarTag(tagDb);

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
