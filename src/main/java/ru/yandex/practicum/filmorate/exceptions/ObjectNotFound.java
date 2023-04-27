package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Logger;

public class ObjectNotFound extends ResponseStatusException {

    public ObjectNotFound(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
        Logger log = Logger.getLogger(getClass().getName());
        log.warning("Response 404, cause: " + msg);
    }
}
