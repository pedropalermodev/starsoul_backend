package br.com.itb.project.starsoul.service;

import br.com.itb.project.starsoul.dto.ConteudoRequestDTO;
import br.com.itb.project.starsoul.dto.ConteudoResponseDTO;
import br.com.itb.project.starsoul.exceptions.BadRequest;
import br.com.itb.project.starsoul.exceptions.NotFound;
import br.com.itb.project.starsoul.model.*;
import br.com.itb.project.starsoul.repository.CategoriaRepository;
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

    public ConteudoService(ConteudoRepository conteudoRepository, Validator validator, TagRepository tagRepository, CategoriaRepository categoriaRepository) {
        this.conteudoRepository = conteudoRepository;
        this.validator = validator;
        this.tagRepository = tagRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Conteudo criarConteudo(ConteudoRequestDTO dto) {

        final Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(dto.getTitulo());
        conteudo.setDescricao(dto.getDescricao());
        conteudo.setCodStatus(dto.getCodStatus());
        conteudo.setTipoConteudo(dto.getTipoConteudo());
        conteudo.setArquivoUrl(dto.getArquivoUrl());
        conteudo.setCaminhoMiniatura(dto.getCaminhoMiniatura());
        conteudo.setDataPublicacao(new Date());

        Set<ConstraintViolation<Conteudo>> violations = validator.validate(conteudo);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Conteudo> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        conteudoRepository.save(conteudo);

        if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
            List<ConteudoCategoria> conteudoCategorias = dto.getCategoriaIds().stream()
                    .map(categoriaId -> {
                        Categoria categoria = categoriaRepository.findById(categoriaId)
                                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + categoriaId));
                        return new ConteudoCategoria(conteudo, categoria);
                    }).collect(Collectors.toList());
            conteudo.getCategorias().addAll(conteudoCategorias);
        }


        // Relacionamentos com Tags
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<ConteudoTag> conteudoTags = dto.getTagIds().stream()
                    .map(tagId -> {
                        Tag tag = tagRepository.findById(tagId)
                                .orElseThrow(() -> new RuntimeException("Tag não encontrada com ID: " + tagId));
                        return new ConteudoTag(conteudo, tag);
                    }).collect(Collectors.toList());
            conteudo.getTags().addAll(conteudoTags);
        }

        return conteudoRepository.save(conteudo);
    }


    public Conteudo listarConteudo(Long id) {
        return conteudoRepository.findById(id).orElseThrow(() -> new NotFound("Conteúdo não encontrado com o id " + id));
    }

    public List<Conteudo> listarTodosConteudos() {
        return conteudoRepository.findAll();
    }

    @Transactional
    public Conteudo atualizarConteudo(Long id, ConteudoRequestDTO dto) {
        Conteudo conteudoDb = conteudoRepository.findById(id).orElseThrow(() -> new NotFound("Conteúdo não encontrado com o id " + id));

        conteudoDb.setCaminhoMiniatura(dto.getCaminhoMiniatura());
        conteudoDb.setTitulo(dto.getTitulo());
        conteudoDb.setDescricao(dto.getDescricao());
        conteudoDb.setCodStatus(dto.getCodStatus());
        conteudoDb.setTipoConteudo(dto.getTipoConteudo());
        conteudoDb.setArquivoUrl(dto.getArquivoUrl());

        // Validar
        Set<ConstraintViolation<Conteudo>> violations = validator.validate(conteudoDb);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Conteudo> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
            }
            throw new BadRequest(errorMessage.toString());
        }

        // Atualizar Categorias
        conteudoDb.getCategorias().clear();
        if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
            List<ConteudoCategoria> novasCategorias = dto.getCategoriaIds().stream()
                    .map(categoriaId -> {
                        Categoria categoria = categoriaRepository.findById(categoriaId)
                                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + categoriaId));
                        return new ConteudoCategoria(conteudoDb, categoria);
                    }).collect(Collectors.toList());
            conteudoDb.getCategorias().addAll(novasCategorias);
        }

        // Atualizar Tags
        conteudoDb.getTags().clear();
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<ConteudoTag> novasTags = dto.getTagIds().stream()
                    .map(tagId -> {
                        Tag tag = tagRepository.findById(tagId)
                                .orElseThrow(() -> new RuntimeException("Tag não encontrada com ID: " + tagId));
                        return new ConteudoTag(conteudoDb, tag);
                    }).collect(Collectors.toList());
            conteudoDb.getTags().addAll(novasTags);
        }

        return conteudoRepository.save(conteudoDb);
    }


    @Transactional
    public void deletarConteudo(Long id) {
        if (!conteudoRepository.existsById(id)) {
            throw new NotFound("Conteúdo não encontrado com o id " + id);
        }
        conteudoRepository.deleteById(id);
    }

    public ConteudoResponseDTO toDto(Conteudo conteudo) {
        ConteudoResponseDTO dto = new ConteudoResponseDTO();
        dto.setId(conteudo.getId());
        dto.setTitulo(conteudo.getTitulo());
        dto.setDescricao(conteudo.getDescricao());
        dto.setCodStatus(conteudo.getCodStatus());
        dto.setTipoConteudo(conteudo.getTipoConteudo());
        dto.setArquivoUrl(conteudo.getArquivoUrl());
        dto.setCaminhoMiniatura(conteudo.getCaminhoMiniatura());
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
