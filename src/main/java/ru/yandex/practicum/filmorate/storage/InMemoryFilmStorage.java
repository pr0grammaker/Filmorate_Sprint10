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
    public Collection<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов, текущий размер: {}", films.size());
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
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
                .releaseDate(film.getReleaseDate() != null ? film.getReleaseDate() : LocalDate.now())
                .build();

        saveFilm(film);
        log.info("Фильм успешно добавлен: {}", film);

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
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

        if (updatedFilm.getLikes() == null) {
            updatedFilm.setLikes(new HashSet<>());
        }

        saveFilm(updatedFilm);
        log.info("Фильм успешно обновлен: {}", updatedFilm);

        return updatedFilm;
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

    public Film getFilmById(Long id) {
        if (id == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        return films.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Collection<Film> getTopFilmOnLikes(Integer count) {
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

