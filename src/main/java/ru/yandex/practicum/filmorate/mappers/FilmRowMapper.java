package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        String title = rs.getString("name");
        String desc = rs.getString("description");

        java.sql.Date sqlDate = rs.getDate("release_date");
        LocalDate release = (sqlDate == null) ? null : sqlDate.toLocalDate();

        int length = rs.getInt("duration");

        int mpaId = rs.getInt("mpa_rating_id");
        String mpaName = rs.getString("mpa_rating_name");
        MpaRating mpa = (mpaId == 0 && mpaName == null) ? null : new MpaRating(mpaId, mpaName);

        Film film = new Film();
        film.setId(id);
        film.setName(title);
        film.setDescription(desc);
        film.setReleaseDate(release);
        film.setDuration(length);
        film.setMpa(mpa);

        return film;
    }
}
