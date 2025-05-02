package br.com.itb.project.starsoul.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteudoTagId implements Serializable {

    private Conteudo conteudo;
    private Tag tag;

}
