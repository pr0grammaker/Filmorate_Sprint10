package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AnyOtherException;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(Long userId, Long friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        log.info("Пользователь с id={} пытается добавить в друзья пользователя с id={}", user.getId(), friendId);

        inMemoryUserStorage.getUserById(friendId);

        if (friendId.equals(user.getId())) {
            log.warn("Попытка добавить себя в друзья: id={}", user.getId());
            throw new AnyOtherException("Нельзя добавить себя в друзья");
        }

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь с id={} уже является другом: id={}", user.getId(), friendId);
            throw new ConditionsNotMetException("Данный пользователь уже имеется у Вас в друзьях");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id={} успешно добавил друга с id={}", user.getId(), friendId);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        log.info("Пользователь с id={} пытается удалить из друзей пользователя с id={}", user.getId(), friendId);

        if (friendId == null) {
            log.warn("Id друга не указан для пользователя id={}", user.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!user.getFriends().contains(friendId)) {
            log.warn("Пользователь с id={} не содержит в друзьях id={}", user.getId(), friendId);
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь с id={} успешно удалил друга с id={}", user.getId(), friendId);
        return user;
    }

    public Collection<User> getFriends(Long userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        log.info("Запрос списка друзей пользователя с id={}", user.getId());
        log.info("У пользователя с id={} найдено {} друзей", user.getId(), user.getFriends().size());

        return user.getFriends().stream()
                .map(inMemoryUserStorage::getUserById)
                .toList();
    }

    public Set<Long> getFriendsForTest(Long userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        return user.getFriends();
    }

    public Collection<User> getGeneralListFriendsWithAFriend(Long userId, Long otherId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User otherUser = inMemoryUserStorage.getUserById(otherId);

        if (user == null || otherUser == null) {
            log.warn("Один из пользователей не найден: user={}, otherUser={}", user, otherUser);
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Запрос общих друзей между пользователями id={} и id={}", user.getId(), otherUser.getId());

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherUserFriends);

        Collection<User> commonFriends = commonFriendIds.stream()
                .map(inMemoryUserStorage::getUserById)
                .filter(Objects::nonNull)
                .toList();

        log.info("Найдено {} общих друзей между пользователями id={} и id={}", commonFriends.size(), user.getId(), otherUser.getId());
        return commonFriends;
    }

    public Set<Long> getGeneralListFriendsWithAFriendForTest(Long userId, Long otherId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User otherUser = inMemoryUserStorage.getUserById(otherId);

        if (user == null || otherUser == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherUserFriends);

        return commonFriends;
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User addUser(User user) {
        checkUser(user);
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        checkUser(newUser);
        return inMemoryUserStorage.updateUser(newUser);
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
            throw new ConditionsNotMetException("Пользователь с таким именем, логинином или email уже существует");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка проверки пользователя: дата рождения в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private boolean userExist(User user) {
        return inMemoryUserStorage.getAllUsers().stream()
                .anyMatch(u -> !u.getId().equals(user.getId()) &&
                        u.getName().equals(user.getName()) ||
                        u.getLogin().equals(user.getLogin()) ||
                        u.getEmail().equals(user.getEmail()));
    }
}
