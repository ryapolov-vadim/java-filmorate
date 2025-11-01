package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {

    private final UserDbStorage userStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User testUser;

    @BeforeEach
    void initUser() {
        testUser = new User();
        testUser.setEmail("example@domain.com");
        testUser.setLogin("exampleLogin");
        testUser.setName("Example Name");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @AfterEach
    void cleanupDb() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.update("TRUNCATE TABLE film_likes");
        jdbcTemplate.update("TRUNCATE TABLE friends");
        jdbcTemplate.update("TRUNCATE TABLE film_genres");
        jdbcTemplate.update("TRUNCATE TABLE films");
        jdbcTemplate.update("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    void createUserTest() {
        User created = userStorage.create(testUser);
        assertNotNull(created.getId());
        assertEquals(testUser.getEmail(), created.getEmail());
        assertEquals(testUser.getLogin(), created.getLogin());
        assertEquals(testUser.getName(), created.getName());
        assertEquals(testUser.getBirthday(), created.getBirthday());
    }

    @Test
    void updateUserTest() {
        User created = userStorage.create(testUser);
        created.setName("Updated Name");
        created.setLogin("UpdatedLogin");
        User updated = userStorage.update(created);
        assertEquals("Updated Name", updated.getName());
        assertEquals("UpdatedLogin", updated.getLogin());
    }

    @Test
    void fetchAllUsersTest() {
        userStorage.create(testUser);
        User another = new User();
        another.setEmail("other@domain.com");
        another.setLogin("otherLogin");
        another.setName("Other User");
        another.setBirthday(LocalDate.of(1985, 5, 5));
        userStorage.create(another);

        List<User> allUsers = userStorage.getAll();
        assertTrue(allUsers.size() >= 2);
    }
}
