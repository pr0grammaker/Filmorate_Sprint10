package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest newUserRequest) {
        return User.builder()
                .email(newUserRequest.getEmail())
                .login(newUserRequest.getLogin())
                .name(newUserRequest.getName())
                .birthday(newUserRequest.getBirthday())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    public static User updateUserFields(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.hasEmail()) {
            if (!updateUserRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(ru|com)$")) {
                throw new ConditionsNotMetException("Имейл должен быть указан и соответствовать формату - example@mail.ru/com");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.hasName()) {
            if (updateUserRequest.getName().isBlank()) {
                throw new ConditionsNotMetException("Имя не может быть пустым");
            }
            user.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.hasLogin()) {
            if (updateUserRequest.getLogin().isBlank()) {
                throw new ConditionsNotMetException("Логин не может быть пустым");
            }
            user.setLogin(updateUserRequest.getLogin());
        }
        if (updateUserRequest.hasBirthday()) {
            if (updateUserRequest.getBirthday().isAfter(LocalDate.now())) {
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }
            user.setBirthday(updateUserRequest.getBirthday());
        }
        return user;
    }
}
