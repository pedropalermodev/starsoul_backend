package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.dto.content.ConteudoRequestDTO;
import br.com.itb.project.starsoul.dto.content.ConteudoResponseDTO;
import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.Conflict;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.*;
import br.com.itb.project.starsoul.repository.CategoriaRepository;
import br.com.itb.project.starsoul.repository.ConteudoCategoriaRepository;
import br.com.itb.project.starsoul.repository.ConteudoRepository;
import br.com.itb.project.starsoul.repository.TagRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConteudoService {

    private final ConteudoRepository conteudoRepository;
    private final Validator validator;
    private final TagRepository tagRepository;
    private final CategoriaRepository categoriaRepository;

    public ConteudoService(
            ConteudoRepository conteudoRepository,
            Validator validator,
            TagRepository tagRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.conteudoRepository = conteudoRepository;
        this.validator = validator;
        this.tagRepository = tagRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private void validarConteudo(Conteudo conteudo) {
        Set<ConstraintViolation<Conteudo>> violations = validator.validate(conteudo);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                    .orElse("Erro de validação no conteúdo");
            throw new BadRequest(errorMessage);
        }
    }

    private List<ConteudoCategoria> mapCategorias(Conteudo conteudo, List<Long> categoriaIds) {
        return categoriaIds.stream()
                .map(id -> {
                    Categoria categoria = categoriaRepository.findById(id)
                            .orElseThrow(() -> new NotFound("Categoria não encontrada com ID: " + id));
                    return new ConteudoCategoria(conteudo, categoria);
                }).toList();
    }

    private List<ConteudoTag> mapTags(Conteudo conteudo, List<Long> tagIds) {
        return tagIds.stream()
                .map(id -> {
                    Tag tag = tagRepository.findById(id)
                            .orElseThrow(() -> new NotFound("Tag não encontrada com ID: " + id));
                    return new ConteudoTag(conteudo, tag);
                }).toList();
    }


    public Conteudo criarConteudo(ConteudoRequestDTO dto) {
        final Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(dto.getTitulo());
        conteudo.setDescricao(dto.getDescricao());
        conteudo.setCodStatus(dto.getCodStatus());
        conteudo.setFormato(dto.getFormato());
        conteudo.setUrl(dto.getUrl());
        conteudo.setDataPublicacao(new Date());


        // Relacionamentos com Categorias
        conteudo.getCategorias().addAll(
                dto.getCategoriaIds() != null ? mapCategorias(conteudo, dto.getCategoriaIds()) : List.of()
        );

        // Relacionamentos com Tags
        conteudo.getTags().addAll(
                dto.getTagIds() != null ? mapTags(conteudo, dto.getTagIds()) : List.of()
        );

        if (conteudoRepository.existsByTitulo(conteudo.getTitulo()) || conteudoRepository.existsByUrl(conteudo.getUrl())) {
            throw new Conflict("Este conteúdo já está cadastrado.");
        }

        validarConteudo(conteudo);

        return conteudoRepository.save(conteudo);
    }


    public Conteudo listarConteudo(Long id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Conteúdo não encontrado com o ID " + id));
    }

    public List<Conteudo> listarTodosConteudos() {
        return conteudoRepository.findAll();
    }

    @Transactional
    public Conteudo atualizarConteudo(Long id, ConteudoRequestDTO dto) {
        Conteudo conteudoDb = conteudoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Conteúdo não encontrado com o ID " + id));

        conteudoDb.setTitulo(dto.getTitulo());
        conteudoDb.setDescricao(dto.getDescricao());
        conteudoDb.setCodStatus(dto.getCodStatus());
        conteudoDb.setFormato(dto.getFormato());
        conteudoDb.setUrl(dto.getUrl());

        // Atualizar categorias
        conteudoDb.getCategorias().clear();
        conteudoDb.getCategorias().addAll(
                dto.getCategoriaIds() != null ? mapCategorias(conteudoDb, dto.getCategoriaIds()) : List.of()
        );

        // Atualizar tags
        conteudoDb.getTags().clear();
        conteudoDb.getTags().addAll(
                dto.getTagIds() != null ? mapTags(conteudoDb, dto.getTagIds()) : List.of()
        );

        if ((!conteudoDb.getTitulo().equals(dto.getTitulo()) && conteudoRepository.existsByTitulo(dto.getTitulo())) ||
                (!conteudoDb.getUrl().equals(dto.getUrl()) && conteudoRepository.existsByUrl(dto.getUrl()))) {
            throw new Conflict("Este conteúdo já está cadastrado.");
        }

        validarConteudo(conteudoDb);

        return conteudoRepository.save(conteudoDb);
    }


    @Transactional
    public void deletarConteudo(Long id) {
        if (!conteudoRepository.existsById(id)) {
            throw new NotFound("Conteúdo não encontrado com o ID " + id);
        }
        conteudoRepository.deleteById(id);
    }

    public ConteudoResponseDTO toDto(Conteudo conteudo) {
        ConteudoResponseDTO dto = new ConteudoResponseDTO();
        dto.setId(conteudo.getId());
        dto.setTitulo(conteudo.getTitulo());
        dto.setDescricao(conteudo.getDescricao());
        dto.setCodStatus(conteudo.getCodStatus());
        dto.setFormato(conteudo.getFormato());
        dto.setUrl(conteudo.getUrl());
        dto.setDataPublicacao(conteudo.getDataPublicacao());

        List<String> categorias = conteudo.getCategorias().stream()
                .map(cc -> cc.getCategoria().getNome())
                .toList();

        List<String> tags = conteudo.getTags().stream()
                .map(ct -> ct.getTag().getNome())
                .toList();

        dto.setCategorias(categorias);
        dto.setTags(tags);

        return dto;
    }

    public List<ConteudoResponseDTO> toDtoList(List<Conteudo> conteudos) {
        return conteudos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
