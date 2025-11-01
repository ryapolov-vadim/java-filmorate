package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesFilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class,
        MpaRatingDbStorage.class, MpaRatingRowMapper.class, LikesFilmDbStorage.class,
        UserRowMapper.class, FriendsDbStorage.class})
public class FilmDbStorageTests {

    private final FilmDbStorage filmDbStorage;
    private Film testFilm;

    @BeforeEach
    void init() {
        testFilm = new Film(0, "My Test Film", "A description for test", LocalDate.of(2000, 1, 1), 120, new MpaRating(1));
        testFilm.addGenre(new Genre(1, "Комедия"));
    }

    @Test
    void createAndFetchFilm() {
        Film saved = filmDbStorage.create(testFilm);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        Film fetched = filmDbStorage.getById(saved.getId());
        assertEquals(testFilm.getName(), fetched.getName());
        assertEquals(testFilm.getDescription(), fetched.getDescription());
        assertEquals(testFilm.getDuration(), fetched.getDuration());
        assertEquals(testFilm.getReleaseDate(), fetched.getReleaseDate());
        assertEquals(testFilm.getMpa().getId(), fetched.getMpa().getId());
        assertTrue(fetched.getGenres().containsAll(testFilm.getGenres()));
    }

    @Test
    void updateExistingFilm() {
        Film saved = filmDbStorage.create(testFilm);
        saved.setName("Updated Film");
        saved.setDescription("Updated description");
        saved.setDuration(90);
        saved.setReleaseDate(LocalDate.of(2010, 10, 10));
        saved.setMpa(new MpaRating(2));
        saved.setGenres(Set.of(new Genre(2, "Драма")));

        Film updated = filmDbStorage.update(saved);
        assertEquals("Updated Film", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(90, updated.getDuration());
        assertEquals(LocalDate.of(2010, 10, 10), updated.getReleaseDate());
        assertEquals(2, updated.getMpa().getId());
        assertTrue(updated.getGenres().contains(new Genre(2, "Драма")));
    }

    @Test
    void fetchAllFilms() {
        filmDbStorage.create(testFilm);
        Film another = new Film(0, "Film 2", "Another description", LocalDate.of(2001, 2, 2), 100, new MpaRating(1));
        another.addGenre(new Genre(1, "Комедия"));
        filmDbStorage.create(another);

        List<Film> films = filmDbStorage.getAll();
        assertTrue(films.size() >= 2);
    }
}
