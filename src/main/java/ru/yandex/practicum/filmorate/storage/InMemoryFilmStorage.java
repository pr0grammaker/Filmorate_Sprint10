package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.info("Запрошен список всех фильмов, текущий размер: {}", films.size());
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Попытка добавить новый фильм: {}", film);

        Long id = getNextID();
        film = film.toBuilder()
                .id(id)
                .releaseDate(film.getReleaseDate() != null ? film.getReleaseDate() : LocalDate.now())
                .build();

        saveFilm(film);
        log.info("Фильм успешно добавлен: {}", film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Попытка обновить фильм: {}", newFilm);

        if (newFilm.getId() == null) {
            log.warn("Ошибка обновления: Id фильма не указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ошибка обновления: Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        Film updatedFilm = films.get(newFilm.getId())
                .toBuilder()
                .name(newFilm.getName())
                .description(newFilm.getDescription())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .build();

        if (updatedFilm.getLikes() == null) {
            updatedFilm.setLikes(new HashSet<>());
        }

        saveFilm(updatedFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm);

        return updatedFilm;
    }

    private Long getNextID() {
        long currentMaxID = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return films.values().stream()
                .filter(film -> film.getId() == id)
                .findFirst();
    }

    @Override
    public Collection<Film> getTopFilmOnLikes(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingLong((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void saveFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
    }
}
