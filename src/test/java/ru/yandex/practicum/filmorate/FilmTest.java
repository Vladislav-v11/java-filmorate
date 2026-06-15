package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Film createValidFilm() {
        Film validFilm = new Film();
        validFilm.setName("Valid film");
        validFilm.setDescription("description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(100);
        return validFilm;
    }

    @Test
    @DisplayName("Проверка валидации фильма с пустым названием")
    void testCreateFilmWithEmptyName() {
        Film film = createValidFilm();
        film.setName(" ");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Название должно быть заполнено")));
    }

    @Test
    @DisplayName("Проверка валидации фильма с описанием длиннее 200 символов")
    void testCreateFilmWithTooLongDescription() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Описание не должно быть длиннее 200 символов")));
    }

    @Test
    @DisplayName("Проверка валидации фильма без даты релиза")
    void testCreateFilmWithEmptyRealisedDate() {
        Film film = createValidFilm();
        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата релиза должна быть указна")));
    }

    @Test
    @DisplayName("Проверка валидации фильма с неположительной продолжительностью")
    void testCreateFilmWithNegativeDuration() {
        Film film = createValidFilm();
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Продолжительность фильма должна быть положительным числом")));
    }
}
