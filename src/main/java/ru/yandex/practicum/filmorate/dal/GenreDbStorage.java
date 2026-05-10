package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private final static String GET_ALL_GENRES_QUERY = """
            SELECT *
            FROM genres
            """;

    private final static String GET_GENRE_BY_ID_QUERY = """
            SELECT *
            FROM genres
            WHERE id = ?
            """;


    private final static String GENRE_EXIST_QUERY = """
            SELECT COUNT(*)
            FROM genres
            WHERE id = ?
            """;

    public GenreDbStorage(JdbcTemplate jdbc,
                          RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getAllGenres() {
        return findMany(GET_ALL_GENRES_QUERY);
    }

    public Optional<Genre> getGenreById(long genreId) {
        return findOne(GET_GENRE_BY_ID_QUERY, genreId);
    }

    public boolean existsById(long genreId) {

        Integer count = jdbc.queryForObject(GENRE_EXIST_QUERY, Integer.class, genreId);

        return count > 0 && count != null;
    }
}
