package br.com.itb.project.starsoul.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;


@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id"
)
@Entity
@Table(name = "conteudo_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ConteudoTagId.class)
public class ConteudoTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "conteudoId")
    private Conteudo conteudo;

    @Id
    @ManyToOne
    @JoinColumn(name = "tagId")
    private Tag tag;

}
