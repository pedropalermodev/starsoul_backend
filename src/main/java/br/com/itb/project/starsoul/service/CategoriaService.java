package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.Conflict;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.Categoria;
import br.com.itb.project.starsoul.model.Conteudo;
import br.com.itb.project.starsoul.model.ConteudoCategoria;
import br.com.itb.project.starsoul.repository.CategoriaRepository;
import br.com.itb.project.starsoul.repository.ConteudoCategoriaRepository;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ConteudoCategoriaRepository conteudoCategoriaRepository;
    private final ConteudoRepository conteudoRepository;
    private final Validator validator;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            ConteudoRepository conteudoRepository,
            ConteudoCategoriaRepository conteudoCategoriaRepository,
            Validator validator
    ) {
        this.categoriaRepository = categoriaRepository;
        this.conteudoRepository = conteudoRepository;
        this.conteudoCategoriaRepository = conteudoCategoriaRepository;
        this.validator = validator;
    }

    private void validarCategoria(Categoria categoria) {
        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação na categoria");
            throw new BadRequest(errorMessage);
        }
    }

    public Categoria criarCategoria(Categoria categoria) {

        validarCategoria(categoria);

        if (categoriaRepository.existsByNome(categoria.getNome())) {
            throw new Conflict("Esta categoria já está cadastrada.");
        }

        if (categoria.getDescricao() != null && categoria.getDescricao().length() < 10) {
            throw new BadRequest("A descrição deve ter pelo menos 10 caracteres.");
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria listarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Categoria não encontrada com o id " + id));
    }

    public List<Categoria> listarTodasCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria atualizarCategoria(Long id, Categoria categoriaAtualizado) {
        Categoria categoriaDb = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Categoria não encontrada com o id " + id));

        boolean statusMudouParaInativo =
                !categoriaDb.getCodStatus().equalsIgnoreCase("Inativo") &&
                categoriaAtualizado.getCodStatus().equalsIgnoreCase("Inativo");

        boolean statusMudouParaAtivo =
                !categoriaDb.getCodStatus().equalsIgnoreCase("Ativo") &&
                        categoriaAtualizado.getCodStatus().equalsIgnoreCase("Ativo");

        categoriaDb.setNome(categoriaAtualizado.getNome());
        categoriaDb.setDescricao(categoriaAtualizado.getDescricao());
        categoriaDb.setCodStatus(categoriaAtualizado.getCodStatus());

        validarCategoria(categoriaDb);

        if (!categoriaDb.getNome().equals(categoriaAtualizado.getNome()) &&
                categoriaRepository.existsByNome(categoriaAtualizado.getNome())) {
            throw new Conflict("Esta categoria já está cadastrada.");
        }

        if (categoriaAtualizado.getDescricao() != null && categoriaAtualizado.getDescricao().length() < 10) {
            throw new BadRequest("A descrição deve ter pelo menos 10 caracteres.");
        }

        Categoria categoriaSalva = categoriaRepository.save(categoriaDb);

        if (statusMudouParaInativo) {
            List<ConteudoCategoria> vinculos = conteudoCategoriaRepository.findByCategoria(categoriaDb);
            for (ConteudoCategoria vinculo : vinculos) {
                Conteudo conteudo = vinculo.getConteudo();

                if (!conteudo.getCodStatus().equals("Inativo")) {
                    conteudo.setCodStatus("Suspenso");
                    conteudoRepository.save(conteudo);
                }
            }
        }

        if (statusMudouParaAtivo) {
            List<ConteudoCategoria> vinculos = conteudoCategoriaRepository.findByCategoria(categoriaDb);
            for (ConteudoCategoria vinculo : vinculos) {
                Conteudo conteudo = vinculo.getConteudo();
                if (!conteudo.getCodStatus().equals("Inativo")) {
                    conteudo.setCodStatus("Ativo");
                    conteudoRepository.save(conteudo);
                }
            }
        }

        return categoriaSalva;
    }

    @Transactional
    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new NotFound("Categoria não encontrada com o id " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
