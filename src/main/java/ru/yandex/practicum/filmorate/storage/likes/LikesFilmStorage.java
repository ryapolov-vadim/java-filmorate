package ru.yandex.practicum.filmorate.storage.likes;

import java.util.List;

public interface LikesFilmStorage {

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);

    List<Long> getLikesById(Long filmId);
}
