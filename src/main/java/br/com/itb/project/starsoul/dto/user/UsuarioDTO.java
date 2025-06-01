package br.com.itb.project.starsoul.dto.user;

import br.com.itb.project.starsoul.model.Usuario;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String tipoConta;
    private String apelido;
    private String dataNascimento;
    private String genero;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipoConta = usuario.getTipoConta();
        this.apelido = usuario.getApelido();
        this.dataNascimento = usuario.getDataNascimento() != null
                ? usuario.getDataNascimento().toString()
                : null;
        this.genero = usuario.getGenero();
    }


    // Getters And Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
