package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.services.UserService;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getAllFriendsOfUser(@PathVariable("userId") long userId) {
        return userService.getAllFriends(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody NewUserRequest newUserRequest) {
        return userService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") long userId){
        userService.deleteUser(userId);
    }

    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId,
                              @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(userId, updateUserRequest);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(
            @PathVariable long id,
            @PathVariable long friendId
    ) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto deleteFriend(
            @PathVariable("id") long id,
            @PathVariable("friendId") long friendId
    ) {
        return userService.deleteFriend(id, friendId);
    }



    @GetMapping("{id}/friends/common/{otherId}")
    public List<UserDto> getGeneralListFriendsWithAFriend(
            @PathVariable("id") long id,
            @PathVariable("otherId") long otherId
    ) {
        return userService.getGeneralListFriendsWithAFriend(id, otherId);
    }
}