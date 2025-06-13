package br.com.itb.project.starsoul.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuarioId", "conteudoId"})
})
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuarioId", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "conteudoId", nullable = false)
    private Conteudo conteudo;

    private boolean favoritado;

    @Column(name = "dataUltimoAcesso", nullable = false)
    private LocalDateTime dataUltimoAcesso;

    @Column(name = "numeroVisualizacoes")
    private int numeroVisualizacoes;

    public Historico() {}

    public Historico(Usuario usuario, Conteudo conteudo) {
        this.usuario = usuario;
        this.conteudo = conteudo;
        this.favoritado = false;
        this.numeroVisualizacoes = 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Conteudo getConteudo() {
        return conteudo;
    }

    public void setConteudo(Conteudo conteudo) {
        this.conteudo = conteudo;
    }

    public boolean isFavoritado() {
        return favoritado;
    }

    public void setFavoritado(boolean favoritado) {
        this.favoritado = favoritado;
    }

    public LocalDateTime getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(LocalDateTime dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    public int getNumeroVisualizacoes() {
        return numeroVisualizacoes;
    }

    public void setNumeroVisualizacoes(int numeroVisualizacoes) {
        this.numeroVisualizacoes = numeroVisualizacoes;
    }
}
