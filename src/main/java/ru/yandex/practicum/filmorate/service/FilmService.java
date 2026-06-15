package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmLikesRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.ValidateRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikesRepository filmLikesRepository;
    private final GenreRepository genreRepository;
    private final ValidateRepository validateRepository;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmLikesRepository filmLikesRepository,
                       GenreRepository genreRepository,
                       ValidateRepository validationRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikesRepository = filmLikesRepository;
        this.genreRepository = genreRepository;
        this.validateRepository = validationRepository;
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        genreRepository.loadGenresForFilms(films);
        return films;
    }

    public Film createFilm(Film film) {
        log.info("Создание нового фильма: {}", film.getName());
        validateFilm(film);

        validateRepository.validateMpaExists(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            validateRepository.validateGenresExist(genreIds);
        }
        Film createdFilm = filmStorage.createFilm(film);
        genreRepository.saveFilmGenres(createdFilm.getId(), createdFilm.getGenres());
        log.info("Фильм создан с ID: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма с ID: {}", film.getId());
        validateFilm(film);

        validateRepository.validateMpaExists(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            validateRepository.validateGenresExist(genreIds);
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        genreRepository.saveFilmGenres(updatedFilm.getId(), updatedFilm.getGenres());
        log.info("Фильм с ID {} успешно обновлен", updatedFilm.getId());
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
        genreRepository.loadGenresForFilm(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        filmLikesRepository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmLikesRepository.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("Запрошены {} популярных фильмов", count);
        if (count <= 0) {
            log.warn("Запрошено недопустимое количество фильмов: {}", count);
            throw new ValidationException("Количество фильмов должно быть положительным числом");
        }

        List<Film> popularFilms = filmLikesRepository.getPopularFilms(count);
        genreRepository.loadGenresForFilms(popularFilms);
        log.debug("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
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
