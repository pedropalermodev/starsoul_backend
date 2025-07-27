package br.com.itb.project.starsoul.dto.user;

import jakarta.validation.constraints.NotBlank;

public class DailyDTO {

    @NotBlank(message = "O humor é obrigatório.")
    private String humor;

    @NotBlank(message = "A anotação é obrigatório.")
    private String anotacao;

    private Long usuarioId;

    public DailyDTO() {}

    public DailyDTO(String humor, String anotacao, Long usuarioId) {
        this.humor = humor;
        this.anotacao = anotacao;
        this.usuarioId = usuarioId;
    }

    // GETTERS AND SETTERS


    public String getHumor() {
        return humor;
    }

    public void setHumor(String humor) {
        this.humor = humor;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
