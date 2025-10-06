package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        log.info("Получен Http-запрос на возврат всех фильмов {}", films);

        log.info("Успешно обработан Http-запрос на возврат всех фильмов {}", films);
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен Http-запрос на создание фильма {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Успешно обработан Http-запрос на создание фильма {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен Http-запрос на обновление фильма {}", film);
        Film film1 = films.get(film.getId());
        if (film1 == null) {
            String error = String.format("Фильм с таким Id %d ненайден", film.getId());
            log.warn(error);
            throw new ValidationException(error);
        }

        film1.setName(film.getName());
        film1.setDescription(film.getDescription());
        film1.setReleaseDate(film.getReleaseDate());
        film1.setDuration(film.getDuration());
        log.info("Успешно обработан Http-запрос на обновление фильма {}", film);
        return film;
    }

    private Long getNextId() {
        long currentMaxId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
