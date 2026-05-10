package ru.yandex.practicum.filmorate.dto.rating;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDto {
    private long id;
    private String name;
}
