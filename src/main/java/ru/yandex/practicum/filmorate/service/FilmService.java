package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAllFilms() {
        log.debug("Запрос на получение всех фильмов");
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film film) {
        log.info("Создание нового фильма: {}", film.getName());
        validateFilm(film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Фильм создан с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма с ID: {}", film.getId());
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        if (updatedFilm == null) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        log.info("Фильм с ID {} успешно обновлен", film.getId());
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        log.debug("Получение фильма с ID: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        Film film = getFilmById(filmId);
        userService.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже ставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        log.info("Фильм {} получил лайк от пользователя {}. Всего лайков: {}",
                filmId, userId, film.getLikes().size());
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        Film film = getFilmById(filmId);
        userService.getUserById(userId);

        boolean hadLike = film.getLikes().contains(userId);
        film.getLikes().remove(userId);

        if (hadLike) {
            log.info("У фильма {} удален лайк от пользователя {}. Всего лайков: {}",
                    filmId, userId, film.getLikes().size());
        } else {
            log.debug("Пользователь {} не ставил лайк фильму {}", userId, filmId);
        }
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("Запрошены {} популярных фильмов", count);
        return filmStorage.findAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка создать фильм с датой релиза {} (минимальная допустимая: {})",
                    film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        log.debug("Валидация фильма {} прошла успешно", film.getName());
    }
}
