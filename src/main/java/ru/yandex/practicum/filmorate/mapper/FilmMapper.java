package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.Duration;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest newFilmRequest) {
        return Film.builder()
                .name(newFilmRequest.getName())
                .description(newFilmRequest.getDescription())
                .releaseDate(newFilmRequest.getReleaseDate())
                .genres(newFilmRequest.getGenres())
                .mpa(newFilmRequest.getMpa())
                .duration(Duration.ofMinutes(newFilmRequest.getDuration()))
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .genres(film.getGenres())
                .mpa(film.getMpa())
                .duration(film.getDuration().toMinutes())
                .likes(film.getLikes().size())
                .build();

    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest updateFilmRequest) {
        if (updateFilmRequest.hasName()) {
            if (updateFilmRequest.getName().isBlank()) {
                throw new ConditionsNotMetException("Название не может быть пустым");
            }
            film.setName(updateFilmRequest.getName());
        }

        if (updateFilmRequest.hasDescription()) {
            if (updateFilmRequest.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            film.setDescription(updateFilmRequest.getDescription());
        }

        if (updateFilmRequest.hasReleaseDate()) {
            film.setReleaseDate(updateFilmRequest.getReleaseDate());
        }

        if (updateFilmRequest.hasGenre()) {
            film.setGenres(updateFilmRequest.getGenres());
        }

        if (updateFilmRequest.hasRating()) {
            film.setMpa(updateFilmRequest.getMpa());
        }
        return film;
    }
}