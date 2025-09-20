package ru.yandex.practicum.filmorate.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidationOfTheReleaseDate implements ConstraintValidator<ReleaseDateValid, LocalDate> {
    private static final LocalDate MOVIE_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        return !localDate.isBefore(MOVIE_RELEASE_DATE);
    }
}
