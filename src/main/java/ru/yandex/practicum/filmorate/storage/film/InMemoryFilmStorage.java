package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Long id = newFilm.getId();
        if (!films.containsKey(id)) {
            log.warn("Попытка обновить несуществующий фильм ID: {}", id);
            return null;
        }
        films.put(id, newFilm);
        log.info("Обновлен фильм с ID: {}, название: {}", id, newFilm.getName());
        return newFilm;

    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Фильм ID {} не найден", id);
        }
        return Optional.ofNullable(film);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
