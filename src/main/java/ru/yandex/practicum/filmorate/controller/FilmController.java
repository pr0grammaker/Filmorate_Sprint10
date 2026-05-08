package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.services.FilmService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;


    @GetMapping
    public List<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilm(@PathVariable("filmId") long filmId){
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addMovie(@RequestBody NewFilmRequest newFilmRequest) {
        return filmService.addFilm(newFilmRequest);
    }

    @PutMapping("/{filmId}")
    public FilmDto updateMovie(@PathVariable("filmId") long filmId,
                               @RequestBody UpdateFilmRequest updateFilmRequest) {
        return filmService.updateFilm(filmId, updateFilmRequest);
    }

    @PutMapping("{filmId}/like/{userId}")
    public FilmDto likeTheFilm(
            @PathVariable long filmId,
            @PathVariable long userId
    ) {
        return filmService.likeTheFilm(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public FilmDto deleteLikeTheFilm(
            @PathVariable long filmId,
            @PathVariable long userId
    ) {
        return filmService.deleteLikeTheFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getTopFilmOnLikes(
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        return filmService.getTopFilmOnLikes(count);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") long filmId){
        filmService.deleteFilm(filmId);
    }

}
