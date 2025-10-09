package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAll() {
        log.debug("Вызван метод getAll() в UserService");
        List<User> userList = userStorage.getAll();
        log.info("Возвращено пользователей {} из хранилища", userList.size());
        return userList;
    }

    public User create(User user) {
        log.debug("Вызван метод create() в UserStorage");
        List<User> userList = userStorage.getAll();
        for (User user1 : userList) {
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

        User result = userStorage.create(user);
        log.info("Создан пользователь {}, в UserService", user);
        return result;
    }

    public User update(User user) {
        log.debug("Вызван метод update() в UserService");
        User userStorageById  = userStorage.getById(user.getId());
        if (userStorageById == null) {
            String error = String.format("Пользователь с таким Id %d ненайден", user.getId());
            log.warn(error);
            throw new NotFoundException(error);
        }

        List<User> userList = userStorage.getAll();
        for (User user1 : userList) {
            if (!user1.getId().equals(user.getId()) && user1.getEmail().equalsIgnoreCase(user.getEmail())) {
                String error = String.format("Пользователь с таким email %s существует", user.getEmail());
                log.warn(error);
                throw new NotFoundException(error);
            }
            if (!user1.getId().equals(user.getId()) && user1.getLogin().equalsIgnoreCase(user.getLogin())) {
                String error = String.format("Пользователь с таким логином %s существует", user.getLogin());
                log.warn(error);
                throw new NotFoundException(error);
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User result = userStorage.update(user);
        log.info("Обновлён пользователь {}, в UserService", user);
        return result;
    }

    public User addFriend(Long id, Long friendId) {
        log.debug("Вызван метод addFriend() в UserService");
        User user = userStorage.getById(id);
        User userFriend = userStorage.getById(friendId);

        if (user == null) {
            String error = String.format("Пользователь с таким ID: %d не найден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (userFriend == null) {
            String error = String.format("Пользователь \"Друг\" с таким ID: %d не найден", friendId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        if (user.getFriends().contains(friendId) && userFriend.getFriends().contains(id)) {
            String error = String.format("Пользователь с ID: {}, уже дружит с пользователем c ID: {} ", id, friendId);
            log.warn(error);
            throw new ValidationException(error);
        }

        user.getFriends().add(friendId);
        log.info("Пользователь с ID: {}, в UserService, успешно добавлен в друзья к пользователю с ID:{}", friendId, id);

        userFriend.getFriends().add(id);
        log.info("Пользователь с ID: {}, в UserService, успешно добавлен в друзья к пользователю с ID:{}", id, friendId);
        return user;
    }

    public User removeFriend(Long id, Long friendId) {
        log.debug("Вызван метод removeFriend() в UserService");
        User user = userStorage.getById(id);
        User userFriend = userStorage.getById(friendId);

        if (user == null) {
            String error = String.format("Пользователь с таким ID: %d не найден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (userFriend == null) {
            String error = String.format("Пользователь \"Друг\" с таким ID: %d не найден", friendId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        user.getFriends().remove(friendId);
        log.info("Пользователь с ID: {}, в UserService, успешно удалён из друзей у пользователю с ID: {}", friendId, id);

        userFriend.getFriends().remove(id);
        log.info("Пользователь с ID: {}, в UserService, успешно удалён из друзей у пользователю с ID: {}", id, friendId);

        return user;
    }

    public List<User> getFriends(Long id) {
        log.debug("Вызван метод getFriends() в UserService");
        User user = userStorage.getById(id);

        if (user == null) {
            String error = String.format("Пользователь с таким ID: %d не найден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }

        List<User> userList = userStorage.getAll().stream()
                .filter(user1 -> user.getFriends().contains(user1.getId()))
                .toList();
        log.info("Выполнен возврат всех друзей пользователя с ID: {}, в UserService", id);

        return userList;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.debug("Вызван метод getCommonFriends() в UserService");
        User user = userStorage.getById(id);
        User userOther = userStorage.getById(otherId);

        if (user == null) {
            String error = String.format("Пользователь с таким ID: %d не найден", id);
            log.warn(error);
            throw new NotFoundException(error);
        }
        if (userOther == null) {
            String error = String.format("Пользователь с таким ID: %d не найден", otherId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        List<User> userList = userStorage.getAll().stream()
                .filter(user1 ->
                        user.getFriends().contains(user1.getId()) && userOther.getFriends().contains(user1.getId()))
                .toList();

        log.info("Выполнен возврат общих друзей пользователя с ID: {}, с пользователем с ID: {}, в UserService",
                id, otherId);
        return userList;
    }
}
