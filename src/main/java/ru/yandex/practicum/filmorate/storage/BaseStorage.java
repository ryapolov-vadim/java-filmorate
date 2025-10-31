package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class BaseStorage<T> {
    protected final JdbcTemplate jdbc;

    protected T findOne(String query, RowMapper<T> rowMapper, Object... args) {
        try {
            T result = jdbc.queryForObject(query, rowMapper, args);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    protected List<T> findMany(String query, RowMapper<T> rowMapper, Object... args) {
        return jdbc.query(query, rowMapper, args);
    }

    protected boolean delete(String query, RowMapper<T> rowMapper, Object... args) {
        int rowsAffected = jdbc.update(query, args);
        return rowsAffected > 0;
    }

    protected void update(String query, Object... args) {
        int rowsAffected = jdbc.update(query, args);
        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, Object... args) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
