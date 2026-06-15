package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final JdbcTemplate jdbcTemplate;

    public void addFriend(long userId, long friendId) {
        String checkSql = "SELECT COUNT(*) FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId, friendId, userId);
        if (count != null && count > 0) {
            throw new ValidationException("Запрос уже отправлен или вы уже друзья");
        }
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, FriendshipStatus.PENDING.ordinal() + 1); // если в БД 1=PENDING
    }

    // Подтверждение дружбы (получатель подтверждает)
    public void acceptFriend(long userId, long friendId) {
        String selectSql = "SELECT status_id FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer status;
        try {
            status = jdbcTemplate.queryForObject(selectSql, Integer.class, friendId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Входящий запрос на дружбу не найден");
        }
        if (status == null || status != FriendshipStatus.PENDING.ordinal() + 1) {
            throw new ValidationException("Запрос уже обработан или не существует");
        }

        String updateSql = "UPDATE friendships SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(updateSql, FriendshipStatus.CONFIRMED.ordinal() + 1, friendId, userId);

        String insertSql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, userId, friendId, FriendshipStatus.CONFIRMED.ordinal() + 1);
    }

    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM friendships f " +
                "JOIN users u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friendships f1 ON u.user_id = f1.friend_id " +
                "JOIN friendships f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId, otherUserId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}