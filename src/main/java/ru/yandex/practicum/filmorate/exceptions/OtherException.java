package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class OtherException extends ResponseStatusException {
    public OtherException(String msg) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, msg);
        log.warn("Response 500, cause: {}", msg);
    }
}
