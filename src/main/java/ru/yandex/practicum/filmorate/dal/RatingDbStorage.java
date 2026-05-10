package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage extends BaseRepository<Rating> implements RatingStorage {

    private final static String GET_ALL_RATINGS_QUERY = """
            SELECT *
            FROM ratings
            """;
    private final static String GET_RATING_BY_ID_QUERY = """
            SELECT *
            FROM ratings
            WHERE id = ?
            """;

    private static final String RATING_EXIST_QUERY = """
            SELECT COUNT(*)
            FROM ratings
            WHERE id = ?
            """;


    public RatingDbStorage(JdbcTemplate jdbc,
                           RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> getAllRatings() {
        return findMany(GET_ALL_RATINGS_QUERY);
    }

    public Optional<Rating> findRatingById(int ratingId) {
        return findOne(GET_RATING_BY_ID_QUERY, ratingId);
    }

    public boolean mpaExist(long mpaId) {
        Integer count = jdbc.queryForObject(RATING_EXIST_QUERY, Integer.class, mpaId);
        return count > 0 && count != null;
    }
}
