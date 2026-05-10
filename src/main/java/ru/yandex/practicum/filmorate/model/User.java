package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Builder.Default
    private Map<Long, FriendshipStatus> friends = new HashMap<>();

}
