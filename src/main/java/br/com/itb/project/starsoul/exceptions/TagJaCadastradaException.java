package br.com.itb.project.starsoul.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TagJaCadastradaException extends RuntimeException {

    public TagJaCadastradaException(String message) {
        super(message);
    }

}
