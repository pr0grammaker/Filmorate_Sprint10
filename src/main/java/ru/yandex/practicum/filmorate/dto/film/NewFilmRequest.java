package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.serializers.DurationMinutesSerializer;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class NewFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Genre> genres;
    private Rating mpa;

    @JsonSerialize(using = DurationMinutesSerializer.class)
    private long duration;
}



