package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();


    @Override
    public Collection<User> findAllUsers() {
        log.info("Запрошен список всех пользователей, текущий размер: {}", users.size());
        return users.values();
    }

    @Override
    public User save(User user) {
        log.info("Попытка добавить нового пользователя: {}", user);

        Long id = getNextID();
        user = user.toBuilder()
                .id(id)
                .build();

        saveUser(user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Попытка обновить пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Ошибка обновления: Id пользователя не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Ошибка обновления: Пользователь с id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId()).toBuilder()
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .name(newUser.getName())
                .birthday(newUser.getBirthday())
                .build();

        // на всякий если вдруг null будет
        if (oldUser.getFriends() == null) {
            oldUser.setFriends(new HashMap<>());
        }

        saveUser(oldUser);
        log.info("Пользователь успешно обновлён: {}", oldUser);
        return oldUser;
    }

    private Long getNextID() {
        long currentMaxID = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }

    @Override
    public Optional<User> findById(long id) {
        return users.values().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    private void saveUser(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }
        users.put(user.getId(), user);
    }


}