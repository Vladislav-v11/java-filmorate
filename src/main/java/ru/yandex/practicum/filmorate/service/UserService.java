package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Для пользователя с логином {} установлено имя из логина", user.getLogin());
        }
        User newUser = userStorage.createUser(user);
        log.info("Добавлен пользователь ID {}", user.getId());
        return newUser;
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя ID: {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User updatedUser = userStorage.updateUser(user);
        if (updatedUser == null) {
            throw new NotFoundException("Пользователь ID " + user.getId() + " не найден");
        }
        log.info("Пользователь ID {} успешно обновлен", user.getId());
        return updatedUser;
    }

    public User getUserById(Long id) {
        log.debug("Получение пользователя ID: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь {} добавляет в друзья {}", userId, friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (userId.equals(friendId)) {
            log.warn("Пользователь {} пытается добавить самого себя в друзья", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь {} уже в друзьях у пользователя {}", friendId, userId);
            throw new ValidationException("Пользователь уже в друзьях");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        boolean userHadFriend = user.getFriends().contains(friendId);
        boolean friendHadUser = friend.getFriends().contains(userId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        if (userHadFriend || friendHadUser) {
            log.info("Пользователи {} и {} больше не друзья", userId, friendId);
        } else {
            log.debug("Пользователи {} и {} не были друзьями", userId, friendId);
        }
    }

    public List<User> getFriends(Long userId) {
        log.debug("Получение списка друзей пользователя {}", userId);
        User user = getUserById(userId);

        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Поиск общих друзей пользователей {} и {}", userId, otherId);
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        Set<Long> commonFriendIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());

        log.debug("Найдено {} общих друзей", commonFriendIds.size());
        return commonFriendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
