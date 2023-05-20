package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class ObjectNotFoundException extends ResponseStatusException {
    public ObjectNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
        log.warn("Response 404, cause: {}", msg);
    }
}
