package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов, текущий размер: {}", films.size());
        return films.values();
    }

    @PutMapping
    public Film updateMovie(@RequestBody Film newFilm) {
        log.info("Попытка обновить фильм: {}", newFilm);

        if (newFilm.getId() == null) {
            log.warn("Ошибка обновления: Id фильма не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка обновления: Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        try {
            checkMovie(newFilm);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки фильма перед обновлением: {}", e.getMessage());
            throw e;
        }

        Film updatedFilm = films.get(newFilm.getId())
                .toBuilder()
                .name(newFilm.getName())
                .description(newFilm.getDescription())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .build();

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm);

        return updatedFilm;
    }

    @PostMapping
    public Film addMovie(@RequestBody Film film) {
        log.info("Попытка добавить новый фильм: {}", film);

        try {
            checkMovie(film);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки фильма перед добавлением: {}", e.getMessage());
            throw e;
        }

        Long id = getNextID();
        film = film.toBuilder()
                .id(id)
                .releaseDate(LocalDate.now())
                .build();

        films.put(id, film);
        log.info("Фильм успешно добавлен: {}", film);

        return film;
    }

    private void checkMovie(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Проверка фильма провалена: название пустое");
            throw new ConditionsNotMetException("Название не может быть пустым");
        }

        if (nameExist(film)) {
            log.warn("Проверка фильма провалена: фильм с таким названием уже существует");
            throw new ConditionsNotMetException("Фильм с таким названием уже существует");
        }

        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            log.warn("Проверка фильма провалена: описание слишком длинное или пустое");
            throw new ConditionsNotMetException("Описание слишком длинное или пустое");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Проверка фильма провалена: дата релиза раньше 28.12.1895");
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28.12.1895");
        }

        if (film.getDuration() == null || film.getDuration().toMinutes() <= 0) {
            log.warn("Проверка фильма провалена: длительность не положительная либо пустая");
            throw new ConditionsNotMetException("Длительность должна быть положительной и не пустой");
        }
    }

    private boolean nameExist(Film newFilm) {
        return films.values().stream()
                .anyMatch(film -> !film.getId().equals(newFilm.getId()) &&
                        film.getName().equals(newFilm.getName()));
    }

    private Long getNextID() {
        long currentMaxID = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}
