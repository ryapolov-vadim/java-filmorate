package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValid;

import java.time.LocalDate;

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
}
