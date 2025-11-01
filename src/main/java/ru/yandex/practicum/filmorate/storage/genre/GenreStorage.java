package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getById(int genreId);

    List<Genre> getAll();

    String getNameById(int id);

    List<Genre> getByFilmId(long id);
}
