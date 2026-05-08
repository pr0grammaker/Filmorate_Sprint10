package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingMapper {
    public static RatingDto maptoRatingDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .name(rating.getName())
                .build();
    }
}
