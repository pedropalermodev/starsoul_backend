package br.com.itb.project.starsoul.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id"
)
@Entity
@Table(name = "conteudo")
public class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "O título é obrigatório.")
    @Size(min = 10, message = "O titulo deve ter pelo menos 10 caracteres.")
    private String titulo;

    @Column(nullable = false, length = 10)
    private String codStatus;

    @Column(nullable = false)
    @Size(max = 5, message = "O tipo deve ter no máximo 5 caracteres.")
    private String formato;

    @Column(nullable = false)
    private String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dataPublicacao;

    @OneToMany(mappedBy = "conteudo", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<ConteudoCategoria> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "conteudo", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<ConteudoTag> tags = new ArrayList<>();


    // Nullable

    @Column(nullable = true)
    @Size(min = 10, message = "A descrição deve ter pelo menos 10 caracteres.")
    private String descricao;


    // Getters And Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(String codStatus) {
        this.codStatus = codStatus;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ConteudoCategoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<ConteudoCategoria> categorias) {
        this.categorias = categorias;
    }

    public List<ConteudoTag> getTags() {
        return tags;
    }

    public void setTags(List<ConteudoTag> tags) {
        this.tags = tags;
    }

}
