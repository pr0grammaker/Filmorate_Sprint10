package ru.yandex.practicum.filmorate.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateUserRequest {
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasName() {
        return !(name == null);
    }

    public boolean hasLogin() {
        return !(login == null);
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }
    // посмотреть насчет строгого формата даты по типу 13.06.2000 либо забить главное чтобы тесты postman проходили

}
