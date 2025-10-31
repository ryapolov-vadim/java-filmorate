package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MpaRating {
    private int id;
    private String name;

    public MpaRating(int id) {
        this.id = id;
    }

    public MpaRating(int id, String mpaRatingName) {
        this.id = id;
        this.name = mpaRatingName;
    }
}
