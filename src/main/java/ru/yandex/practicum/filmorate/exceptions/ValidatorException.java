package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class ValidatorException extends ResponseStatusException {

    public ValidatorException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
        log.error("Response 400, cause: {}", message);
    }
}
