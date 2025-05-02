package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final Validator validator;

    public CategoriaService(CategoriaRepository categoriaRepository, Validator validator) {
        this.categoriaRepository = categoriaRepository;
        this.validator = validator;
    }

    public Categoria criarCategoria(Categoria categoria) {
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Categoria> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria listarCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new NotFound("Categoria não encontrado com o id " + id));
    }

    public List<Categoria> listarTodosCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, Categoria categoriaAtualizado) {
        Categoria categoriaDb = categoriaRepository.findById(id).orElseThrow(() -> new NotFound("Categoria não encontrado com o id " + id));

        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoriaAtualizado);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Categoria> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        categoriaDb.setNome(categoriaAtualizado.getNome());
        categoriaDb.setDescricao(categoriaAtualizado.getDescricao());
        categoriaDb.setCodStatus(categoriaAtualizado.getCodStatus());

        return categoriaRepository.save(categoriaDb);
    }

    @Transactional
    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new NotFound("Categoria não encontrado com o id " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
