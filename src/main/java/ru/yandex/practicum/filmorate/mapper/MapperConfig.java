package ru.yandex.practicum.filmorate.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class MapperConfig { // Spring не видит поэтому сделал отдельный класс и обьявил их тут как бины

    @Bean
    public RowMapper<User> userRowMapper() {
        return (ResultSet rs, int rowNum) -> User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getObject("birthday", LocalDate.class))
                .friends(new HashMap<>())
                .build();
    }



    @Bean
    public RowMapper<Film> filmRowMapper() {
        return (ResultSet rs, int rowNum) -> Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getObject("release_date", LocalDate.class))
                .genres(rs.getObject("genre_id") != null
                        ? new HashSet<>(Set.of(
                        Genre.builder()
                                .id(rs.getLong("genre_id"))
                                .build()))
                        : new HashSet<>())

                .mpa(rs.getObject("rating_id") != null
                        ? Rating.builder()
                        .id(rs.getLong("rating_id"))
                        .build()
                        : null)

                .duration(
                        rs.getObject("duration") != null
                                ? Duration.ofMinutes(rs.getLong("duration"))
                                : null)
                .likes(new HashSet<>())
                .build();
    }

    @Bean
    public RowMapper<Genre> genreRowMapper() {
        return (ResultSet rs, int rowNum) -> Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    @Bean
    public RowMapper<Rating> ratingRowMapper() {
        return (ResultSet rs, int rowNum) -> Rating.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }


}
