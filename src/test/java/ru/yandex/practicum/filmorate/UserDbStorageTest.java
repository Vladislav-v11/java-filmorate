package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    @DisplayName("Создание пользователя и поиск по ID")
    void createUserAndFindById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.createUser(user);
        assertThat(created.getId()).isNotNull();

        Optional<User> found = userStorage.getUserById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя возвращает пустой Optional")
    void getUserByIdReturnsEmptyForNonExistent() {
        Optional<User> found = userStorage.getUserById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Обновление существующего пользователя")
    void updateUser() {
        User user = new User();
        user.setEmail("update@example.com");
        user.setLogin("oldlogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.createUser(user);

        created.setLogin("newlogin");
        created.setName("New Name");
        userStorage.updateUser(created);

        Optional<User> updated = userStorage.getUserById(created.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getLogin()).isEqualTo("newlogin");
        assertThat(updated.get().getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя")
    void updateNonExistentUserThrowsException() {
        User user = new User();
        user.setId(999L);
        user.setEmail("ghost@example.com");
        user.setLogin("ghost");
        user.setName("Ghost");
        user.setBirthday(LocalDate.now());

        assertThatThrownBy(() -> userStorage.updateUser(user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void findAllUsers() {
        Collection<User> users = userStorage.findAllUsers();
        assertThat(users).isNotNull();
    }
}