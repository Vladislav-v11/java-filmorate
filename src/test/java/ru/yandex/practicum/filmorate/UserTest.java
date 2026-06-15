package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private User createValidUser() {
        User user = new User();
                user.setEmail("valid@email");
                user.setLogin("login");
                user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @DisplayName("Проверка валидации пользователя с незаполненным email")
    @Test
    void testCreateUserWithEmptyEmail() {
        User user = createValidUser();
        user.setEmail(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Адрес электронной почты должен быть указан")));
    }

    @DisplayName("Проверка валидации пользователя с некорректным email")
    @Test
    void testCreateUserWithInvalidEmail() {
        User user = createValidUser();
        user.setEmail("invalidemail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Электронная почта должна быть формата электронного адреса", violations.iterator().next().getMessage());
    }

    @DisplayName("Проверка валидации пользователя с пустым логином")
    @Test
    void testCreateUserWithEmptyLogin() {
        User user = createValidUser();
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Логин не должен содержать пробелы")));
    }

    @DisplayName("Проверка валидации пользователя с логином содержащим пробел")
    @Test
    void testCreateUserWithLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("test user");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Логин не должен содержать пробелы")));
    }

    @DisplayName("Проверка валидации пользователя с датой рождения в будущем")
    @Test
    void testCreateUserWithBirthdayInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата рождения не может быть в будущем")));
    }
}
