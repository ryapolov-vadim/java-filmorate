package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public UserService(UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }


    public List<User> getAll() {
        log.debug("Вызван метод getAll() в UserService");
        List<User> userList = userStorage.getAll();
        log.info("Возвращено пользователей {} из БД", userList.size());
        return userList;
    }

    public User getUserById(long id) {
        log.debug("Вызван метод getUserById() в UserService");
        User user = userStorage.getById(id);
        log.info("Возвращен пользователь {} из БД", user);
        return user;
    }

    public User create(User user) {
        log.debug("Вызван метод create() в UserStorage");
        checkName(user);
        User result = userStorage.create(user);
        log.info("Создан пользователь {}, в UserService", user);
        return result;
    }

    public User update(User user) {
        log.debug("Вызван метод update() в UserService");
        log.info("Проверяем, что пользователь {} существует ", user);
        getUserById(user.getId());
        checkName(user);
        User result = userStorage.update(user);
        log.info("Обновлён пользователь {}, в UserService", user);
        return result;
    }

    public void addFriend(Long id, Long friendId) {
        log.debug("Вызван метод addFriend() в UserService");
        checkSameUser(id, friendId);
        getUserById(id);
        getUserById(friendId);
        friendsStorage.addFriend(id, friendId);
        log.info("Пользователь с ID: {}, в UserService, успешно добавлен в друзья к пользователю с ID:{}", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        log.debug("Вызван метод removeFriend() в UserService");
        checkSameUser(id, friendId);
        getUserById(id);
        getUserById(friendId);

        friendsStorage.removeFriend(id, friendId);
        log.info("Пользователь с ID: {}, в UserService, успешно удалён из друзей у пользователя с ID: {}", id, friendId);
    }

    public List<User> getFriends(Long id) {
        log.debug("Вызван метод getFriends() в UserService");
        getUserById(id);
        List<User> userList = friendsStorage.getFriends(id);
        log.info("Выполнен возврат всех друзей пользователя с ID: {}, в UserService", id);
        return userList;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.debug("Вызван метод getCommonFriends() в UserService");
        checkSameUser(id, otherId);
        getUserById(id);
        getUserById(otherId);

        List<User> userList = friendsStorage.getCommonFriends(id, otherId);
        log.info("Выполнен возврат общих друзей пользователя с ID: {}, с пользователем с ID: {}, в UserService",
                id, otherId);
        return userList;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя не указано при создании, устанавливаем имя по умолчанию - логин");
            user.setName(user.getLogin());
        }
    }

    private void checkSameUser(long userId, long friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья.");
        }
    }
}
