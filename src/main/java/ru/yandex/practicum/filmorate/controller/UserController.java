package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        log.info("Получен запрос на создание пользователя");

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Для пользователя с логином {} установлено имя из логина", user.getLogin());
        }

        user.setId(getNextId());
        log.info("Добавлен пользователь{}", user.getId());

        users.put(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {

        log.info("Получен запрос на изменение пользователя");

        User oldUser = users.get(newUser.getId());

        if (newUser.getId() == null) {
            log.error("Не указан id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("Указан несуществующий id");
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (!newUser.getEmail().equals(oldUser.getEmail())) {
            boolean emailExists = users.values().stream()
                    .anyMatch(u -> u.getEmail().equals(newUser.getEmail()));
            if (emailExists) {
                log.error("Указанный Email занят");
                throw new ValidationException("Этот Email уже занят");
            }
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return users.values();
    }
}
