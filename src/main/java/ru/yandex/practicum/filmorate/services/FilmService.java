package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Film likeTheFilm(Long filmId, Long userId) {
        log.info("Попытка поставить лайк пользователем id={} фильму id={}", userId, filmId);

        Film film = inMemoryFilmStorage.getFilmById(filmId);
        inMemoryUserStorage.getUserById(userId);

        if (!film.getLikes().add(userId)) {
            log.warn("Пользователь id={} уже поставил лайк фильму id={}", userId, filmId);
            throw new ConditionsNotMetException("Пользователь уже поставил лайк");
        }

        log.info("Лайк успешно поставлен: пользователь id={} -> фильм id={}", userId, filmId);
        return film;
    }


    public Film deleteLikeTheFilm(Long filmId, Long userId) {
        log.info("Попытка удалить лайк пользователем id={} фильму id={}", userId, filmId);

        Film film = inMemoryFilmStorage.getFilmById(filmId);
        inMemoryUserStorage.getUserById(userId);

        if (!film.getLikes().remove(userId)) {
            log.warn("Пользователь id={} еще не ставил лайк фильму id={}", userId, filmId);
            throw new ConditionsNotMetException("Пользователь еще не поставил лайк");
        }

        log.info("Лайк успешно удален: пользователь id={} -> фильм id={}", userId, filmId);
        return film;
    }

    public Collection<Film> getTopFilmOnLikes(Integer count) {
        log.info("Запрошен топ {} фильмов по количеству лайков", count);
        log.info("Найдено {} фильмов", inMemoryFilmStorage.getTopFilmOnLikes(count).size());
        return inMemoryFilmStorage.getTopFilmOnLikes(count);
    }

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        try {
            checkMovie(film);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки фильма перед добавлением: {}", e.getMessage());
            throw e;
        }

        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        try {
            checkMovie(film);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка проверки фильма перед обновлением: {}", e.getMessage());
            throw e;
        }

        return inMemoryFilmStorage.updateFilm(film);
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
        return inMemoryFilmStorage.getAllFilms().stream()
                .anyMatch(film -> !film.getId().equals(newFilm.getId()) &&
                        film.getName().equals(newFilm.getName()));
    }

}
