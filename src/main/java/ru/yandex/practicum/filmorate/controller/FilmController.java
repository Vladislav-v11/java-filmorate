package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        log.info("Получен запрос на добавление фильма");

        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getId());
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Указана дата релиза фильма ранее 28.12.1895");
            throw new ValidationException("Дата релиза не может быть ранее 28.12.1895");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {

        log.info("Получен запрос на изменение фильма");

        if (newFilm.getId() == null) {
            log.error("Не указан ID фильма");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.error("Указан не существующий айди");
            throw new ValidationException("Фильм с таким id не существует");
        }

        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Изменен фильм {}", newFilm.getId());
        return newFilm;
    }

    @GetMapping
    public Collection<Film> findAlliFilms() {
        return films.values();
    }
}
