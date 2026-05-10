package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User user);

    Collection<User> findAllUsers();

    Optional<User> findById(long userId);
}
