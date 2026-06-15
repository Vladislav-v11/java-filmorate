package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable @Positive Long id) {
        log.info("GET /users/{} - получение пользователя по ID", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users - создание пользователя: {}", user.getLogin());
        //return userService.createUser(user);
        User createdUser = userService.addUser(user);
        log.info("Пользователь успешно создан с id: {}", createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users - обновление пользователя с ID: {}", user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long friendId) {
        log.info("PUT /users/{}/friends/{} - добавление в друзья", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long friendId) {
        log.info("DELETE /users/{}/friends/{} - удаление из друзей", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Long id) {
        log.info("GET /users/{}/friends - получение списка друзей", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long otherId) {
        log.info("GET /users/{}/friends/common/{} - поиск общих друзей", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        Collection<User> users = userService.getAllUsers();
        log.info("Возвращено {} пользователей", users.size());
        return users;
    }

    @PutMapping("/{id}/friends/{friendId}/accept")
    public void acceptFriend(@PathVariable @Positive Long id,
                             @PathVariable @Positive Long friendId) {
        log.info("PUT /users/{}/friends/{}/accept - подтверждение дружбы", id, friendId);
        userService.acceptFriend(id, friendId);
    }
}
