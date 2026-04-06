package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей, текущий размер: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Попытка добавить нового пользователя: {}", user);

        try {
            checkUser(user);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки пользователя перед добавлением: {}", e.getMessage());
            throw e;
        }

        Long id = getNextID();
        user = user.toBuilder()
                .id(id)
                .build();

        users.put(id, user);
        log.info("Пользователь успешно добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("Попытка обновить пользователя: {}", newUser);

        if (newUser.getId() == null) {
            log.warn("Ошибка обновления: Id пользователя не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Ошибка обновления: Пользователь с id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        try {
            checkUser(newUser);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки пользователя перед обновлением: {}", e.getMessage());
            throw e;
        }

        User oldUser = users.get(newUser.getId()).toBuilder()
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .name(newUser.getName())
                .birthday(newUser.getBirthday())
                .build();

        users.put(oldUser.getId(), oldUser);
        log.info("Пользователь успешно обновлён: {}", oldUser);
        return oldUser;
    }

    private void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() ||
                !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(ru|com)$")) {
            log.warn("Ошибка проверки пользователя: некорректный email");
            throw new ConditionsNotMetException("Имейл должен быть указан и соответствовать формату - example@mail.ru/com");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка проверки пользователя: логин пустой");
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано, присваиваем email как имя: {}", user.getEmail());
            user.setName(user.getEmail());
        }

        if (userExist(user)) {
            log.warn("Ошибка проверки пользователя: пользователь с таким именем или логином уже существует");
            throw new ConditionsNotMetException("Пользователь с таким именем, логинином или емайлом уже существует");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка проверки пользователя: дата рождения в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private boolean userExist(User user) {
        return users.values().stream()
                .anyMatch(u -> !u.getId().equals(user.getId()) &&
                        u.getName().equals(user.getName()) ||
                        u.getLogin().equals(user.getLogin()) ||
                        u.getEmail().equals(user.getEmail()));
    }

    private Long getNextID() {
        long currentMaxID = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}