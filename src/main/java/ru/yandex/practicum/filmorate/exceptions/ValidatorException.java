package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Logger;

public class ValidatorException extends ResponseStatusException {

    public ValidatorException(String message){
        super(HttpStatus.BAD_REQUEST, message);
        Logger log = Logger.getLogger(getClass().getName());
        log.warning("Response 400" + ", cause: " + message);
    }
}
