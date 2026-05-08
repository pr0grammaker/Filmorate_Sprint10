//package ru.yandex.practicum.filmorate.mappers;
//
//import org.springframework.jdbc.core.RowMapper;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Genre;
//import ru.yandex.practicum.filmorate.model.Rating;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.Duration;
//import java.util.HashSet;
//import java.util.Set;
//
//public class FilmRowMapper implements RowMapper<Film> {
//
//    @Override
//    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
//        return Film.builder()
//                .id(rs.getLong("id"))
//                .name(rs.getString("name"))
//                .description(rs.getString("description"))
//                .releaseDate(rs.getDate("release_date").toLocalDate())
//                .genres(rs.getObject("genre_id") != null
//                        ? new HashSet<>(Set.of(
//                        Genre.builder()
//                                .id(rs.getLong("genre_id"))
//                                .build()))
//                        : new HashSet<>())
//
//                .mpa(rs.getObject("rating_id") != null
//                        ? Rating.builder()
//                        .id(rs.getLong("rating_id"))
//                        .build()
//                        : null)
//
//                .duration(
//                        rs.getObject("duration") != null
//                                ? Duration.ofMinutes(rs.getLong("duration"))
//                                : null)
//                .likes(new HashSet<>())
//                .build();
//    }
//
//}