package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("user_id"));
        u.setLogin(rs.getString("login"));
        u.setEmail(rs.getString("email"));
        u.setName(rs.getString("name"));

        java.sql.Date bd = rs.getDate("birthday");
        if (bd != null) {
            u.setBirthday(bd.toLocalDate());
        } else {
            u.setBirthday(null);
        }
        return u;
    }
}
