package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getById(int genreId) {
        return genreStorage.getById(genreId);
    }

    public List<Genre> getByFilmId(long id) {
        return genreStorage.getByFilmId(id);
    }
}
