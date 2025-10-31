package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Genre {
    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }

    public Genre(int id, String genre) {
        this.id = id;
        this.name = genre;
    }
}
