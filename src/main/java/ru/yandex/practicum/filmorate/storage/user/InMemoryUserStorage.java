package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь ID: {}, логин: {}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Обновлен пользователь ID: {}, логин: {}", id, user.getLogin());
            return user;
        }
        log.warn("Попытка обновить пользователя с несуществующим ID: {}", id);
        return null;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void deleteUser(Long id) {
        if (users.remove(id) != null) {
            log.info("Удален пользователь с ID: {}", id);
        } else {
            log.warn("Попытка удалить несуществующего пользователя с ID: {}", id);
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
