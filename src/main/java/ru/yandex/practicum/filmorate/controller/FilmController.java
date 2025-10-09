package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Получен Http-запрос на возврат всех фильмов");
        List<Film> filmList = filmService.getAll();
        log.info("Успешно обработан Http-запрос на возврат всех фильмов");
        return filmList;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен Http-запрос на создание фильма {}", film);
        Film result = filmService.create(film);
        log.info("Успешно обработан Http-запрос на создание фильма {}", film);
        return result;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен Http-запрос на обновление фильма {}", film);
        Film result = filmService.update(film);
        log.info("Успешно обработан Http-запрос на обновление фильма {}", film);
        return result;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен Http-запрос на добавление лайка фильму с ID: {}, от пользователя с ID: {}", id, userId);
        Film result = filmService.addLike(id, userId);
        log.info("Успешно обработан Http-запрос на добавление лайка фильму. Фильм ID: {}, пользователя ID: {}",
                id, userId);
        return result;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен Http-запрос на удаление лайка у фильма с ID: {}, от пользователя с ID: {}", id, userId);
        Film result = filmService.removeLike(id, userId);
        log.info("Успешно обработан Http-запрос на удаление лайка у фильма. Фильм ID: {}, пользователя ID: {}",
                id, userId);
        return result;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен Http-запрос на возврат {} популярных фильмов", count);
        List<Film> filmList = filmService.getPopularFilms(count);
        log.info("Успешно обработан Http-запрос на возврат {} популярных фильмов", count);
        return filmList;
    }
}
