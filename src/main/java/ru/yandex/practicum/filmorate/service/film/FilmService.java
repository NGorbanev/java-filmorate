package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.OtherException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.compare;
@Slf4j
@Service
public class FilmService {

    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage")UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // proxying methods (validator check added here)
    public Film postFilm(Film film) {
        if (validator.validate(film)) {
            return filmStorage.postFilm(film);
        } else return film; // unreachable case
    }

    //todo if everything works, this should be deleted:
    public Film putFilmNoArgs(Film film) {
        if (validator.validate(film) && filmIdValidator(film.getId())) {
            //return filmStorage.putFilmNoArgs(film);
            return filmStorage.putFilm(film.getId(), film);
        } else return film; // unreachable case
    }

    public Film putFilm(int id, Film film) {
        if (validator.validate(film) && filmIdValidator(id)) {
            return filmStorage.putFilm(id, film);
        } else return film; // unreachable case
    }

    public Collection<Film> getFilmsAsArrayList() {
        return filmStorage.getFilmsAsArrayList();
    }

    public Film getFilmById(int id) {
        if (filmIdValidator(id)) {
            return filmStorage.getFilmById(id);
        } else throw new ObjectNotFoundException(String.format("Film id=%s was not found", id));
    }

    //service methods
    private boolean filmIdValidator(int filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.warn("Film not found for id={}", filmId);
            throw new ObjectNotFoundException(String.format("Film id=%s was not found", filmId));
        } else return true;
    }

    private boolean userIdValidator(int userId) {
        if (userStorage.getUser(userId) == null) {
            log.warn("User not found for id={}", userId);
            throw new ObjectNotFoundException(String.format("User id=%s was not found", userId));
        } else return true;
    }

    // business logic methods
    //todo check
    public Film addLike(int filmId, int userId) {
        if (filmIdValidator(filmId) && userIdValidator(userId)) {
            Film film = filmStorage.putFilm(filmId, filmStorage.getFilmById(filmId).addLike(userId));
            log.info("Like successfully added to film id={} from user id={}", filmId, userId);
            return film;
        } else throw new OtherException(String.format("Like adding error, FilmId=%s, UserId=%s", filmId, userId));
    }

    public Film removeLike(int filmId, int userId) {
        log.info("Request for like removal to film id={} of user id={} received", filmId, userId);
        if (filmIdValidator(filmId) && userIdValidator(userId)) {
            Film film = filmStorage.putFilm(filmId, filmStorage.getFilmById(filmId).removeLike(userId));
            log.info("Like from user id={} to film id={} was successfully removed", userId, filmId);
            return film;
        } else throw new OtherException(String.format("Like removal failed, FilmId=%s, UserId=%s", filmId, userId));
    }

    //public List<Film> getTopLikedFilms(int numOfFilms) {
    //    return null;
    //}


    public List<Film> getTopLikedFilms(int numOfFilms) {
        return filmStorage.getFilmsAsArrayList().stream()
                .sorted((o0, o1) -> compare(o1.getLikeSet().size(), o0.getLikeSet().size()))
                .limit(numOfFilms)
                .collect(Collectors.toList());
    }


    // film attributes request (mpa ratings & genres)
    public List<Genre> getAllGenres() {
        log.trace("Request serviced");
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        log.trace("Request serviced");
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa() {
        log.trace("Request serviced");
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        log.trace("Request serviced");
        return filmStorage.getMpaById(id);
    }
}
