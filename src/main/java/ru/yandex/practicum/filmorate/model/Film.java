package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "название не может быть пустым")
    private String name;

    @Size(max = 200, message = "максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна для заполнения")
    @ReleaseDateValid
    private LocalDate releaseDate;

    @NotNull(message = "продолжительность фильма обязательна для заполнения")
    @Positive(message = "продолжительность фильма должна быть положительным числом")
    private Integer duration;

    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private MpaRating mpa;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
