package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;


@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String GET_ALL_FILMS_QUERY = """
            SELECT *
            FROM films
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO films(name, description, release_date, duration, genre_id, rating_id)
            VALUES (?,?,?,?,?,?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, genre_id = ?, rating_id = ?
            WHERE id = ?
            """;

    private static final String FILM_LIKE_QUERY = """
            INSERT INTO film_likes (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String FILM_DISLIKE_QUERY = """
            DELETE FROM film_likes
            WHERE film_id = ? AND user_id = ?
            """;

    private static final String MOST_POPULAR_FILMS_QUERY = """
            SELECT f.*
            FROM films f
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT ?
            """;

    private static final String FIND_FILM_BY_ID_QUERY = """
            SELECT *
            FROM films
            WHERE id = ?
            """;

    private static final String LIKE_EXIST_QUERY = """
            SELECT COUNT(*)
            FROM film_likes
            WHERE film_id = ? AND user_id = ?
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM films
            WHERE id = ?
            """;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> getFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
    }

    public Film createFilm(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toMinutes(),
                film.getGenres() != null && !film.getGenres().isEmpty()
                        ? film.getGenres().iterator().next().getId()
                        : null,

                film.getMpa() != null
                        ? film.getMpa().getId()
                        : null
        );
        film.setId(id);
        return film;
    }

    public Optional<Film> findFilmById(long filmId) {
        return findOne(FIND_FILM_BY_ID_QUERY, filmId);
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toMinutes(),
                film.getGenres() != null && !film.getGenres().isEmpty()
                        ? film.getGenres().iterator().next().getId()
                        : null,

                film.getMpa() != null
                        ? film.getMpa().getId()
                        : null,

                film.getId()
        );

        return film;
    }

    public void addLike(long filmId, long userId) {
        update(FILM_LIKE_QUERY, filmId, userId);
    }

    public boolean isLikeExists(long filmId, long userId) {
        Integer count = jdbc.queryForObject(LIKE_EXIST_QUERY, Integer.class, filmId, userId);

        return count != null && count > 0;
    }

    public void removeLike(long filmId, long userId) {
        int rows = jdbc.update(FILM_DISLIKE_QUERY, filmId, userId);

        if (rows == 0) {
            throw new NotFoundException("Лайк не найден");
        }
    }

    public List<Film> getTopFilmOnLikes(int count) {
        return findMany(MOST_POPULAR_FILMS_QUERY, count);
    }

    public boolean delete(long filmId) {
        return delete(DELETE_QUERY, filmId);
    }
}
