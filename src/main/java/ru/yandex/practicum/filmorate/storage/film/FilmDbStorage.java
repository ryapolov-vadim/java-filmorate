package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import java.sql.Date;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaRatingStorage;

    private static final String FIND_ALL_SQL = """
            SELECT f.*, m.* 
            FROM films f 
            LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id
            ORDER BY f.film_id
            """;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         GenreStorage genreStorage,
                         MpaRatingStorage mpaRatingStorage) {
        super(jdbcTemplate);
        this.genreStorage = genreStorage;
        this.mpaRatingStorage = mpaRatingStorage;
    }

    @Override
    public List<Film> getAll() {
        List<Film> list = jdbc.query(FIND_ALL_SQL, new FilmRowMapper());
        enrichWithGenres(list);
        enrichWithLikes(list);
        return list;
    }

    @Override
    public Film create(Film film) {
        // ensure MPA and genre names are consistent
        if (film.getMpa() != null) {
            film.setMpa(mpaRatingStorage.getById(film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(g -> g.setName(genreStorage.getNameById(g.getId())));
        }

        long newId = insert(
                "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId()
        );
        film.setId(newId);

        if (film.getGenres() != null) {
            String insertGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre g : film.getGenres()) {
                jdbc.update(insertGenre, film.getId(), g.getId());
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaRatingStorage.getById(film.getMpa().getId()));
        }

        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_rating_id=? WHERE film_id=?";
        update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId()
        );
        // note: genres updating outside (if needed) — keep behavior consistent with previous impl
        return film;
    }

    @Override
    public Film getById(long id) {
        String sql = """
                SELECT f.*, m.* 
                FROM films f 
                LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id 
                WHERE f.film_id = ?
                """;
        Film f = findOne(sql, new FilmRowMapper(), id);
        if (f == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        enrichWithGenres(List.of(f));
        enrichWithLikes(List.of(f));
        return f;
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = """
                SELECT f.*, m.mpa_rating_id, m.mpa_rating_name 
                FROM films f
                LEFT JOIN film_likes fl ON f.film_id = fl.film_id
                JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id
                GROUP BY f.film_id, m.mpa_rating_id, m.mpa_rating_name
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT ?
                """;
        List<Film> top = jdbc.query(sql, new FilmRowMapper(), count);
        enrichWithGenres(top);
        enrichWithLikes(top);
        return top;
    }

    private void enrichWithLikes(List<Film> films) {
        if (films == null || films.isEmpty()) return;

        List<Long> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        String inClause = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "SELECT film_id, user_id FROM film_likes WHERE film_id IN (" + inClause + ")";

        Map<Long, Set<Long>> map = new HashMap<>();
        jdbc.query(sql, rs -> {
            long filmId = rs.getLong("film_id");
            long userId = rs.getLong("user_id");
            map.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        for (Film f : films) {
            f.setLikes(map.getOrDefault(f.getId(), new HashSet<>()));
        }
    }

    private void enrichWithGenres(List<Film> films) {
        if (films == null || films.isEmpty()) return;

        List<Long> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        String inClause = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = String.format("""
                SELECT fg.film_id, g.genre_id, g.genre_name
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.genre_id
                WHERE fg.film_id IN (%s)
                ORDER BY g.genre_id
                """, inClause);

        Map<Long, LinkedHashSet<Genre>> map = new HashMap<>();
        jdbc.query(sql, rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            map.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        });

        for (Film f : films) {
            f.setGenres(map.getOrDefault(f.getId(), new LinkedHashSet<>()));
        }
    }
}
