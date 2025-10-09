package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public User create(User user) {
        log.debug("Добавление нового пользователя {} в InMemoryUserStorage", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен {} в InMemoryUserStorage", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Обновление пользователя {} в InMemoryUserStorage", user);
        User updatedUser = users.get(user.getId());

        updatedUser.setName(user.getName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        log.info("Пользователь {} успешно обновлён в InMemoryUserStorage", user);
        return user;
    }

    @Override
    public User getById(Long id) {
        log.debug("Возвращение пользователя по ID: {} в InMemoryUserStorage",id);
        User user = users.get(id);
        log.info("Пользователь возвращен по ID: {} в InMemoryUserStorage",id);
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
