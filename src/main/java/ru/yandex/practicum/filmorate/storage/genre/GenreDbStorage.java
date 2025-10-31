package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return findMany(sql, new GenreRowMapper());
    }

    @Override
    public Genre getById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        Genre genre = findOne(sql, new GenreRowMapper(), id);
        if (genre == null) {
            throw new NotFoundException("Жанр с id: " + id + " не найден");
        }
        return genre;
    }

    public String getNameById(int id) {
        return getById(id).getName();
    }

    @Override
    public List<Genre> getByFilmId(long id) {
        String sql = """
                    SELECT g.genre_id, g.genre_name
                    FROM film_genres fg
                    JOIN genres g ON fg.genre_id = g.genre_id
                    WHERE fg.film_id = ?
                    ORDER BY g.genre_id
                """;
        return findMany(sql, new GenreRowMapper(), id);
    }
}
