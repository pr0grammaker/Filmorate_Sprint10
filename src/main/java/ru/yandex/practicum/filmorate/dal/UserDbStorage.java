package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;


@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private final static String GET_ALL_QUERY = """
            SELECT *
            FROM users""";
    private final static String FIND_BY_ID_QUERY = """
            SELECT *
            FROM users
            WHERE id = ?""";
    private final static String FIND_BY_EMAIL_QUERY = """
            SELECT *
            FROM users
            WHERE email = ?""";
    private final static String FIND_BY_LOGIN_QUERY = """
            SELECT *
            FROM users
            WHERE login = ?""";
    private final static String GET_ALL_FRIENDS_QUERY = """
            SELECT u.id, u.name, u.login, u.email, u.birthday
            FROM friendships f
            JOIN users u ON u.id = f.friend_id
            WHERE f.user_id = ?
            AND f.status = 'CONFIRMED'""";
    private final static String INSERT_QUERY = """
            INSERT INTO users(email, login, name, birthday)
            VALUES (?, ?, ?, ?)""";

    private final static String UPDATE_QUERY = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?""";
    private final static String DELETE_QUERY = """
            DELETE FROM users
            WHERE id = ?""";

    private static final String INSERT_FRIEND_QUERY = """
            INSERT INTO friendships(user_id, friend_id, status)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_FRIEND_STATUS_QUERY = """
            UPDATE friendships
            SET status = ?
            WHERE user_id = ? AND friend_id = ?
            """;

    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM friendships
            WHERE user_id = ? AND friend_id = ?
            """;

    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT u.*
            FROM friendships f1
            JOIN friendships f2 ON f1.friend_id = f2.friend_id
            JOIN users u ON u.id = f1.friend_id
            WHERE f1.user_id = ?
            AND f2.user_id = ?
            AND f1.status = 'CONFIRMED'
            AND f2.status = 'CONFIRMED'
            """;

    private static final String GET_FRIENDSHIP_STATUS_QUERY = """
            SELECT status
            FROM friendships
            WHERE user_id = ? AND friend_id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbc,
                         RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAllUsers() {
        return findMany(GET_ALL_QUERY);
    }

    public List<User> getAllUserFriends(long userId) {
        return findMany(GET_ALL_FRIENDS_QUERY, userId);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findByLogin(String login) {
        return findOne(FIND_BY_LOGIN_QUERY, login);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public void addFriend(long userId, long friendId, String status) {
        update(INSERT_FRIEND_QUERY, userId, friendId, status);
    }

    public void updateFriendStatus(long userId, long friendId, String status) {
        update(UPDATE_FRIEND_STATUS_QUERY, status, userId, friendId);
    }

    public boolean delete(long userId) {
        return delete(DELETE_QUERY, userId);
    }

    public Optional<String> getFriendshipStatus(long userId, long friendId) {
        return findOne(GET_FRIENDSHIP_STATUS_QUERY, String.class, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return findMany(GET_COMMON_FRIENDS_QUERY, userId, otherId);
    }


}
