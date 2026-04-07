package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

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
}
