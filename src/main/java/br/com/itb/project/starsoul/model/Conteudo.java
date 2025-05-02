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
@Table(name = "conteudos")
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
    private String tipoConteudo;

    @Column(nullable = false)
    private String arquivoUrl;

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
    private String caminhoMiniatura;

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

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public String getArquivoUrl() {
        return arquivoUrl;
    }

    public void setArquivoUrl(String arquivoUrl) {
        this.arquivoUrl = arquivoUrl;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getCaminhoMiniatura() {
        return caminhoMiniatura;
    }

    public void setCaminhoMiniatura(String caminhoMiniatura) {
        this.caminhoMiniatura = caminhoMiniatura;
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
