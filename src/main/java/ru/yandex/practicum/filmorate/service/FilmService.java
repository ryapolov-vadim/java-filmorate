package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesFilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesFilmStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage ratingStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikesFilmStorage likesStorage,
                       GenreStorage genreStorage, MpaRatingStorage ratingStorage,
                       UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.userStorage = userStorage;
    }


    public List<Film> getAll() {
        log.debug("Вызван метод getAll() в FilmService");
        List<Film> filmList = filmStorage.getAll();
        log.info("Получено {} фильмов из хранилища", filmList.size());
        return filmList;
    }

    public Film getFilmById(Long filmId) {
        log.debug("Вызван метод getFilmById() в FilmService");
        return filmStorage.getById(filmId);
    }

    public Film create(Film film) {
        log.debug("Вызван метод create() в FilmService");
        MpaRating mpaRating = ratingStorage.getById(film.getMpa().getId());
        film.setMpa(mpaRating);
        film.getGenres().forEach(genre -> {
            Genre existingGenre = genreStorage.getById(genre.getId());
            if (existingGenre != null) {
                genre.setName(existingGenre.getName());
            } else {
                throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        });
        log.info("Создан фильм {}, в FilmService", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.debug("Вызван метод update() в FilmService");
        getFilmById(film.getId());

        MpaRating mpaRating = ratingStorage.getById(film.getMpa().getId());
        film.setMpa(mpaRating);
        film.getGenres().forEach(genre -> {
            Genre existingGenre = genreStorage.getById(genre.getId());
            if (existingGenre != null) {
                genre.setName(existingGenre.getName());
            } else {
                throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        });
        log.info("Успешно обновлён фильм {} в FilmServic", film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Вызван метод addLike() в FilmService");
        getFilmById(filmId);
        userStorage.getById(userId);

        likesStorage.addLike(filmId, userId);
        log.info("Успешно вызван метод addLike() в FilmServic");
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Вызван метод removeLike() в FilmService");
        getFilmById(filmId);
        userStorage.getById(userId);

        likesStorage.removeLike(filmId, userId);
        log.info("Успешно вызван метод removeLike() в FilmServic");
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Вызван метод getPopularFilms() в FilmService");
        List<Film> popularFilm = filmStorage.getPopular(count);
        log.info("Успешно вызван метод getPopularFilms() в FilmServic");
        return popularFilm;
    }
}
