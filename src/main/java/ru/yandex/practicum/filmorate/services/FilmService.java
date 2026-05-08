package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;

    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
    }


    public List<FilmDto> getAllFilms() {
        return filmDbStorage.getFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        checkMovie(newFilmRequest);
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        film = filmDbStorage.createFilm(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(long filmId, UpdateFilmRequest updateFilmRequest) {
        Film updateFilm = filmDbStorage.findFilmById(filmId)
                .map(film -> FilmMapper.updateFilmFields(film, updateFilmRequest))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        updateFilm = filmDbStorage.update(updateFilm);

        return FilmMapper.mapToFilmDto(updateFilm);

    }

    public FilmDto likeTheFilm(long filmId, long userId) {
        Film film = filmDbStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (filmDbStorage.isLikeExists(filmId, userId)) {
            log.warn("Пользователь id={} уже поставил лайк фильму id={}", userId, filmId);
            throw new ConditionsNotMetException("Пользователь уже поставил лайк");
        }

        filmDbStorage.addLike(filmId, userId);

        return FilmMapper.mapToFilmDto(film);

    }

    public FilmDto deleteLikeTheFilm(long filmId, long userId) {
        Film film = filmDbStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!filmDbStorage.isLikeExists(filmId, userId)) {
            throw new ConditionsNotMetException("Пользователь еще не поставил лайк");
        }

        filmDbStorage.removeLike(filmId, userId);

        return FilmMapper.mapToFilmDto(film);

    }

    public List<FilmDto> getTopFilmOnLikes(int count) {
        return filmDbStorage.getTopFilmOnLikes(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void checkMovie(NewFilmRequest film) {
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

        if (film.getMpa() != null) {
            long mpaId = film.getMpa().getId();

            boolean exists = ratingExist(mpaId);

            if (!exists) {
                throw new NotFoundException("MPA не найден");
            }
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre == null || genre.getId() < 0 || genre.getId() == 0) {
                    throw new ConditionsNotMetException("Жанр не указан корректно");
                }

                boolean exists = genreDbStorage.existsById(genre.getId());
                if (!exists) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
            }
        }

        if (film.getDuration() == 0 || film.getDuration() <= 0) {
            log.warn("Проверка фильма провалена: длительность не положительная либо пустая");
            throw new ConditionsNotMetException("Длительность должна быть положительной и не пустой");
        }
    }

    private boolean ratingExist(long mpaId) {
        return ratingDbStorage.mpaExist(mpaId);
    }

    private boolean nameExist(NewFilmRequest newFilm) {
        return filmDbStorage.getFilms().stream()
                .anyMatch(film -> film.getName().equals(newFilm.getName()));
    }

    public void deleteFilm(long filmId) {
        boolean deleted = filmDbStorage.delete(filmId);

        if (!deleted) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public FilmDto getFilmById(long filmId) {
        Film film = filmDbStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        return FilmMapper.mapToFilmDto(film);
    }
}
