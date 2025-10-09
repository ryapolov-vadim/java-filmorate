package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;
    private User user;

    @BeforeEach
    void setUp() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
        user = new User();
    }

    @Test
    void getAllEmptyList() {
        // Исполнение
        List<User> users = controller.getAll();

        // Проверка
        assertTrue(users.isEmpty(), "Список пользователей должен быть пустым при старте");
    }

    @Test
    void createUserSuccess() {
        // Подготовка
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");

        // Исполнение
        User createdUser = controller.create(user);

        // Проверка
        assertNotNull(createdUser.getId(), "ID пользователя не должен быть null");
        assertEquals("test@example.com", createdUser.getEmail(), "Email не совпадает");
        assertEquals("testuser", createdUser.getLogin(), "Логин не совпадает");
        assertEquals("Test User", createdUser.getName(), "Имя не совпадает");
    }

    @Test
    void createUserSetsNameToLoginIfNameBlank() {
        // Подготовка
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("   "); // пустое имя

        // Исполнение
        User createdUser = controller.create(user);

        // Проверка
        assertEquals("testuser", createdUser.getName(),
                "Если имя пустое, оно должно заменяться на логин");
    }

    @Test
    void createUserDuplicateEmailThrowsException() {
        // Подготовка
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        controller.create(user);

        // Исполнение
        User duplicate = new User();
        duplicate.setEmail("test@example.com"); // тот же email
        duplicate.setLogin("anotherLogin");
        duplicate.setName("Duplicate");

        assertThrows(ValidationException.class,
                () -> controller.create(duplicate),
                "Ожидалось исключение при повторяющемся email");
    }

    @Test
    void updateUserSuccess() {
        // Подготовка
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        User created = controller.create(user);
        created.setName("Updated Name");
        created.setEmail("updated@example.com");
        created.setLogin("updatedLogin");

        // Исполнение
        User updated = controller.update(created);

        assertEquals("Updated Name", updated.getName(), "Имя должно обновиться");
        assertEquals("updated@example.com", updated.getEmail(), "Email должен обновиться");
        assertEquals("updatedLogin", updated.getLogin(), "Логин должен обновиться");
    }

    @Test
    void updateUserNotFoundThrowsException() {
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        controller.create(user);
        // Подготовка
        user.setId(999L);

        // Исполнение
        assertThrows(NotFoundException.class,
                () -> controller.update(user),
                "Ожидалось исключение при обновлении несуществующего пользователя");
    }
}
