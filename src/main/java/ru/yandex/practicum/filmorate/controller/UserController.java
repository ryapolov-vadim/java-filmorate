package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получен Http-запрос на возврат всех пользователей");
        List<User> users = userService.getAll();
        log.info("Успешно обработан Http-запрос на возврат всех пользователей");
        return users;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Получаем юзера с id {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен Http-запрос на создание пользователя {}", user);
        User result = userService.create(user);
        log.info("Успешно обработан Http-запрос на создание пользователя {}", user);
        return result;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен Http-запрос на обновление пользователя {}", user);
        User result = userService.update(user);
        log.info("Успешно обработан Http-запрос на обновление пользователя {}", user);
        return result;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен Http-запрос на добавление пользователя с ID: {}  в друзья к пользователю с ID: {} ",
                id, friendId);
        userService.addFriend(id, friendId);
        log.info("Успешно обработан Http-запрос на добавление пользователя с ID: {}  в друзья к пользователю с ID: {} ",
                id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен Http-запрос на удаление пользователя с ID: {} из друзей с ID: {} ", friendId, id);
        userService.removeFriend(id, friendId);
        log.info("Успешно обработан Http-запрос на удаление пользователя с ID: {} из друзей с ID: {} ", friendId, id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен Http-запрос на возврат списка всех пользователей - друзей, пользователя с ID: {} ", id);
        List<User> result = userService.getFriends(id);
        log.info("Успешно обработан Http-запрос на возврат списка всех пользователей - друзей, пользователя с ID: {} ",
                id);
        return result;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен Http-запрос пользователя с ID: {}  на возврат списка общих друзей c пользователем с ID: {} ",
                id, otherId);
        List<User> result = userService.getCommonFriends(id, otherId);
        log.info("Успешно обработан Http-запрос пользователя с ID: {} на возврат списка общих друзей",
                id);
        return result;
    }
}
