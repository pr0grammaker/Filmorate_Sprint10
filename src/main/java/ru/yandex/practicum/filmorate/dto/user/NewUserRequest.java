package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NewUserRequest {
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
