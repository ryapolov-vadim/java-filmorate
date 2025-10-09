package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAll() {
        log.debug("Вызван метод getAll() в FilmService");
        List<Film> filmList = filmStorage.getAll();
        log.info("Получено {} фильмов из хранилища", filmList.size());
        return filmList;
    }

    public Film create(Film film) {
        log.debug("Вызван метод create() в FilmService");
        Film result = filmStorage.create(film);
        log.info("Создан фильм {}, в FilmService", result);
        return result;
    }

    public Film update(Film film) {
        log.debug("Вызван метод update() в FilmService");
        Film film1 = filmStorage.getById(film.getId());
        if (film1 == null) {
            String error = String.format("Фильм с таким ID: %d ненайден", film.getId());
            log.warn(error);
            throw new NotFoundException(error);
        }

        Film film2 = filmStorage.update(film);
        log.info("Успешно обновлён фильм {} в FilmServic", film2);
        return film;
    }

    public Film addLike(Long id, Long userId) {
        log.debug("Вызван метод addLike() в FilmService");
        Film film1 = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        if (film1 == null) {
            String error = String.format("Фильм с таким Id: %d не найден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (user == null) {
            String error = String.format("Пользователь с таким Id: %d не найден", userId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        if (film1.getLikes().contains(userId)) {
            String error = String.format("Пользователь с Id: %d уже ставил лайк", userId);
            log.warn(error);
            throw new ValidationException(error);
        }

        film1.getLikes().add(userId);
        log.info("Успешно вызван метод addLike() в FilmServic");
        return film1;
    }

    public Film removeLike(Long id, Long userId) {
        log.debug("Вызван метод removeLike() в FilmService");
        Film film1 = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        if (film1 == null) {
            String error = String.format("Фильм с таким Id: %d ненайден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (user == null) {
            String error = String.format("Пользователь с таким Id: %d ненайден",  userId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        if (!film1.getLikes().contains(userId)) {
            String error = String.format("Пользователь с Id: %d не ставил лайк", userId);
            log.warn(error);
            throw new NotFoundException(error); //Возможно ValidationException
        }

        film1.getLikes().remove(userId);
        log.info("Успешно вызван метод removeLike() в FilmServic");
        return film1;
    }

    public List<Film> getPopularFilms(Integer count) {
        log.debug("Вызван метод getPopularFilms() в FilmService");
        List<Film> popularFilm = filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
        log.info("Успешно вызван метод getPopularFilms() в FilmServic");
        return popularFilm;
    }
}
