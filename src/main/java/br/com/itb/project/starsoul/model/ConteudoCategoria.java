package br.com.itb.project.starsoul.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "conteudo_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id"
)
@IdClass(ConteudoCategoriaId.class)
public class ConteudoCategoria {

    @Id
    @ManyToOne
    @JoinColumn(name = "conteudoId")
    private Conteudo conteudo;

    @Id
    @ManyToOne
    @JoinColumn(name = "categoriaId")
    private Categoria categoria;

}