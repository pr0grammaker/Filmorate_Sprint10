package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @PutMapping
    public Film updateMovie(@RequestBody Film newFilm) {
        return inMemoryFilmStorage.updateFilm(newFilm);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addMovie(@RequestBody Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping("{filmId}/like/{userId}")
    public Film likeTheFilm(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        return filmService.likeTheFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public Film deleteLikeTheFilm(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        return filmService.deleteLikeTheFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilmOnLikes(
            @RequestParam(required = false, defaultValue = "10") Integer count
    ){
        return filmService.getTopFilmOnLikes(count);
    }

}
