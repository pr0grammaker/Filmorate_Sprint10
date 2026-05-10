package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UpdateFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Genre> genres;
    private Rating mpa;
    private long duration;


    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasGenre() {
        return !(genres == null);
    }

    public boolean hasRating() {
        return !(mpa == null);
    }


}
