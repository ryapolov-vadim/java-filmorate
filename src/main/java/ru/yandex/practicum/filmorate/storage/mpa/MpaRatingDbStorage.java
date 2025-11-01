package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Repository
public class MpaRatingDbStorage extends BaseStorage<MpaRating> implements MpaRatingStorage {

    public final MpaRatingRowMapper mapper;

    public MpaRatingDbStorage(JdbcTemplate jdbc) {
        super(jdbc);
        this.mapper = new MpaRatingRowMapper();
    }

    @Override
    public List<MpaRating> getAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_rating_id";
        return findMany(sql, mapper);
    }

    @Override
    public MpaRating getById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
        MpaRating rating = findOne(sql, mapper, id);
        if (rating == null) {
            throw new NotFoundException("Рейтинг с id: " + id + " не найден");
        }
        return rating;
    }

    @Override
    public MpaRating getByFilmId(long filmId) {
        String sql = """
                SELECT m.* 
                FROM films f
                JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id
                WHERE f.film_id = ?
                """;
        MpaRating rating = findOne(sql, mapper, filmId);
        if (rating == null) {
            throw new NotFoundException("Рейтинг для фильма с id: " + filmId + " не найден");
        }
        return rating;
    }
}
