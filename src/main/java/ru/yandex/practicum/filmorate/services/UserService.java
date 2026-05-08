package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserDbStorage userDbStorage;

    public UserService(UserDbStorage userDbStorage) {

        this.userDbStorage = userDbStorage;
    }

    public List<UserDto> getUsers() {
        return userDbStorage.findAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getAllFriends(long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userDbStorage.getAllUserFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        checkUser(newUserRequest);
        User user = UserMapper.mapToUser(newUserRequest);
        user = userDbStorage.save(user);

        return UserMapper.mapToUserDto(user);
    }


    public UserDto updateUser(long userId, UpdateUserRequest updateUserRequest) {
        User updateUser = userDbStorage.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, updateUserRequest))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updateUser = userDbStorage.update(updateUser);
        return UserMapper.mapToUserDto(updateUser);
    }

    public UserDto addFriend(long userId, long friendId) {
        User user = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        if (userId == friendId) {
            throw new ConditionsNotMetException("Нельзя добавить себя в друзья");
        }

//        Optional<String> reverse = userDbStorage.getFriendshipStatus(friendId, userId);
//
//        if (reverse.isPresent() && reverse.get().equals("PENDING")) {
//            userDbStorage.updateFriendStatus(friendId, userId, "CONFIRMED");
//            userDbStorage.addFriend(userId, friendId, "CONFIRMED");
//
//        } else {
//            userDbStorage.addFriend(userId, friendId, "PENDING");
//        }
        // Пришлось убрать т.к. в ТЗ написано про односторонюю дружбу оставил на всякий
        userDbStorage.addFriend(userId, friendId, "CONFIRMED");

        return UserMapper.mapToUserDto(user);
    }

    public UserDto deleteFriend(long userId, long friendId) {
        User user = userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

//        if (userDbStorage.getFriendshipStatus(userId, friendId).isEmpty()) {
//            throw new NotFoundException("Дружба не найдена");
//        }
        // это условие мешало тестам поэтому тоже убрал и оставил на всякий
        if (userId == friendId) {
            throw new ConditionsNotMetException("Нельзя удалить самого себя");
        }

        userDbStorage.deleteFriend(userId, friendId);

        return UserMapper.mapToUserDto(user);
    }


    public List<UserDto> getGeneralListFriendsWithAFriend(Long userId, Long otherId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        userDbStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return userDbStorage.getCommonFriends(userId, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();

    }

    private void checkUser(NewUserRequest user) {

        if (user.getEmail() == null || user.getEmail().isBlank() ||
                !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(ru|com)$")) {
            log.warn("Ошибка проверки пользователя: некорректный email");
            throw new ConditionsNotMetException("Имейл должен быть указан и соответствовать формату - example@mail.ru/com");
        }

        if (userEmailExist(user)) {
            log.warn("Ошибка проверки пользователя: пользователь с таким email уже существует");
            throw new ConditionsNotMetException("Пользователь с таким email уже существует");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка проверки пользователя: логин пустой");
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }

        if (userLoginExist(user)) {
            log.warn("Ошибка проверки пользователя: пользователь с таким login уже существует");
            throw new ConditionsNotMetException("Пользователь с таким login уже существует");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя не указано, присваиваем email как имя: {}", user.getEmail());
            user.setName(user.getEmail());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка проверки пользователя: дата рождения в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private boolean userEmailExist(NewUserRequest user) {
        return userDbStorage.findByEmail(user.getEmail()).isPresent();
    }

    private boolean userLoginExist(NewUserRequest user) {
        return userDbStorage.findByLogin(user.getLogin()).isPresent();
    }

    public void deleteUser(long userId) {
        boolean deleted = userDbStorage.delete(userId);

        if (!deleted) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}



