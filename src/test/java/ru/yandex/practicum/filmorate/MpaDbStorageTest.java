package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    @DisplayName("Получение всех MPA рейтингов")
    void getAllMpaRatings() {
        List<MpaRating> ratings = mpaStorage.getAllMpaRatings();
        assertThat(ratings).hasSize(5);
        assertThat(ratings.get(0).getId()).isEqualTo(1);
        assertThat(ratings.get(0).getName()).isIn("G", "0+"); // допускаем US или RU названия
    }

    @Test
    @DisplayName("Получение MPA по существующему ID")
    void getMpaRatingById() {
        MpaRating rating = mpaStorage.getMpaRatingById(2);
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isEqualTo(2);
        assertThat(rating.getName()).isIn("PG", "6+");
    }

    @Test
    @DisplayName("Получение MPA по несуществующему ID вызывает исключение")
    void getMpaRatingByIdNotFound() {
        assertThatThrownBy(() -> mpaStorage.getMpaRatingById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("MPA рейтинг с id 999 не найден");
    }
}