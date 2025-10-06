package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        log.info("Получен Http-запрос на возврат всех пользователей {}", users);

        log.info("Успешно обработан Http-запрос на возврат всех пользователей {}", users);
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен Http-запрос на создание пользователя {}", user);
        for (User user1 : users.values()) {
            if (user1.getEmail().equalsIgnoreCase(user.getEmail())) {
                String error = String.format("Пользователь с таким email %s существует", user.getEmail());
                log.warn(error);
                throw new ValidationException(error);
            }
            if (user1.getLogin().equalsIgnoreCase(user.getLogin())) {
                String error = String.format("Пользователь с таким логином %s существует", user.getLogin());
                log.warn(error);
                throw new ValidationException(error);
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Успешно обработан Http-запрос на создание пользователя {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен Http-запрос на обновление пользователя {}", user);
        User updatedUser = users.get(user.getId());
        if (updatedUser == null) {
            String error = String.format("Пользователь с таким Id %d ненайден", user.getId());
            log.warn(error);
            throw new ValidationException(error);
        }

        for (User user1 : users.values()) {
            if (!user1.getId().equals(user.getId()) && user1.getEmail().equalsIgnoreCase(user.getEmail())) {
                String error = String.format("Пользователь с таким email %s существует", user.getEmail());
                log.warn(error);
                throw new ValidationException(error);
            }
            if (!user1.getId().equals(user.getId()) && user1.getLogin().equalsIgnoreCase(user.getLogin())) {
                String error = String.format("Пользователь с таким логином %s существует", user.getLogin());
                log.warn(error);
                throw new ValidationException(error);
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        log.info("Успешно обработан Http-запрос на обновление пользователя {}", user);
        return user;
    }

    private Long getNextId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
