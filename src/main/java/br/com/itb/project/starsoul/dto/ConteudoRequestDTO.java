package br.com.itb.project.starsoul.dto;

import java.util.ArrayList;
import java.util.List;

public class ConteudoRequestDTO {

    private String titulo;
    private String descricao;
    private String codStatus;
    private String tipoConteudo;
    private String arquivoUrl;
    private String caminhoMiniatura;
    private List<Long> categoriaIds = new ArrayList<>();
    private List<Long> tagIds = new ArrayList<>();

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getCaminhoMiniatura() {
        return caminhoMiniatura;
    }

    public void setCaminhoMiniatura(String caminhoMiniatura) {
        this.caminhoMiniatura = caminhoMiniatura;
    }

    public List<Long> getCategoriaIds() {
        return categoriaIds;
    }

    public void setCategoriaIds(List<Long> categoriaIds) {
        this.categoriaIds = categoriaIds;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
