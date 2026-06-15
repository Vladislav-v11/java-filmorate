package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, MpaDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    @DisplayName("Создание фильма и получение по ID")
    void createFilmAndGetById() {
        MpaRating mpa = mpaStorage.getMpaRatingById(1);
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        Film created = filmStorage.createFilm(film);
        assertThat(created.getId()).isNotNull();

        Optional<Film> found = filmStorage.getFilmById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Film");
    }

    @Test
    @DisplayName("Обновление существующего фильма")
    void updateFilm() {
        MpaRating mpa = mpaStorage.getMpaRatingById(2);
        Film film = new Film();
        film.setName("Old Name");
        film.setDescription("Old Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(mpa);
        Film created = filmStorage.createFilm(film);

        created.setName("New Name");
        created.setDuration(150);
        filmStorage.updateFilm(created);

        Optional<Film> updated = filmStorage.getFilmById(created.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("New Name");
        assertThat(updated.get().getDuration()).isEqualTo(150);
    }

    @Test
    @DisplayName("Обновление несуществующего фильма вызывает исключение")
    void updateNonExistentFilmThrowsException() {
        Film film = new Film();
        film.setId(999L);
        film.setName("Ghost");
        film.setDescription("none");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);
        film.setMpa(new MpaRating(1, "G"));

        assertThatThrownBy(() -> filmStorage.updateFilm(film))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с id 999 не найден");
    }

    @Test
    @DisplayName("Поиск всех фильмов")
    void findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        assertThat(films).isNotNull();
    }
}