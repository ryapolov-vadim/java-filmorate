package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    LocalDate date = LocalDate.of(2020, 1, 1);
    Film film;

    @BeforeEach
    void setUp() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
        film = new Film();
    }

    @Test
    void getAllEmptyList() {
        // Исполнение
        List<Film> films = controller.getAll();

        // Проверка
        assertTrue(films.isEmpty(), "Список фильмов должен быть пустым при старте");
    }

    @Test
    void getAllWithFilms() {
        // Подготовка
        film.setName("James Cameron");
        film.setDescription("The Lord of the Rings");
        film.setReleaseDate(date);
        film.setDuration(120);
        controller.create(film);

        // Исполнение
        List<Film> films = controller.getAll();

        // Проверка
        assertNotNull(films, "Список фильмов не должен быть null");
        assertEquals(1, films.size(), "Размер списка должен быть равен 1");
    }

    @Test
    void createFilmSuccess() {
        // Подготовка
        film.setName("James Cameron");
        film.setDescription("The Lord of the Rings");
        film.setReleaseDate(date);
        film.setDuration(120);

        // Исполнение
        Film createdFilm = controller.create(film);

        // Проверка
        assertNotNull(createdFilm.getId(), "ID фильма не должен быть null");
        assertEquals("James Cameron", createdFilm.getName(), "Названия не совпадают");
        assertEquals("The Lord of the Rings", createdFilm.getDescription(), "Описание не совпадает");
        assertEquals(date, createdFilm.getReleaseDate(), "Дата не совпадает");
        assertEquals(120, createdFilm.getDuration(), "Продолжительность не совпадает");
    }

    @Test
    void updateFilmSuccess() {
        // Подготовка
        film.setName("James Cameron");
        film.setDescription("The Lord of the Rings");
        film.setReleaseDate(date);
        film.setDuration(120);

        Film createdFilm = controller.create(film);

        // Исполнение
        createdFilm.setName("Updated Test Film");
        createdFilm.setDescription("Updated Test Description");
        Film updatedFilm = controller.update(createdFilm);

        // Проверка
        assertEquals("Updated Test Film", updatedFilm.getName(), "Название должно обновиться");
        assertEquals("Updated Test Description", updatedFilm.getDescription(), "Описание должно обновиться");
    }

    @Test
    void updateFilmNotFound() {
        // Подготовка
        film.setId(999L);
        film.setName("Non existing");

        // Исполнение и проверка
        assertThrows(RuntimeException.class,
                () -> controller.update(film),
                "Ожидалось исключение при обновлении несуществующего фильма");
    }
}
