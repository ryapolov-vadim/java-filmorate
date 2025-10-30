package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        log.debug("Извлечение всех фильмов из памяти ({} записей)", films.size());
        return films.values().stream().toList();
    }

    @Override
    public Film create(Film film) {
        log.debug("Добавление нового фильма {}", film);
        film.setId(getNextId());
        films.put(getNextId(), film);
        log.info("Фильм успешно добавлен {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.debug("Обновление фильма {}", film);
        Film film1 = films.get(film.getId());

        film1.setName(film.getName());
        film1.setDescription(film.getDescription());
        film1.setReleaseDate(film.getReleaseDate());
        film1.setDuration(film.getDuration());
        log.info("Фильм успешно обновлён {}", film);
        return film;
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getPopular(Integer userId) {
        return List.of();
    }

    private Long getNextId() {
        long currentMaxId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
