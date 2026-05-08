import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.mapper.MapperConfig;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest(properties = "spring.sql.init.data-locations=classpath:data-test.sql")
@AutoConfigureTestDatabase
@ContextConfiguration(classes = FilmorateApplication.class)
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        MapperConfig.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationForFilmTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    public void testGetFilms() {
        filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        List<Film> films = filmDbStorage.getFilms();

        assertThat(films)
                .isNotEmpty();
    }

    @Test
    public void testCreateFilm() {

        Film film = Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build();

        Film saved = filmDbStorage.createFilm(film);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Film");
    }

    @Test
    public void testFindFilmById() {

        Film film = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        Optional<Film> found = filmDbStorage.findFilmById(film.getId());

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f.getName()).isEqualTo("New Film")
                );
    }

    @Test
    public void testUpdateFilm() {

        Film film = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        film.setName("New Name");
        film.setDescription("New desc");

        Film updated = filmDbStorage.update(film);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("New desc");
    }

    @Test
    public void testAddLike() {

        Film film = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        User user = userDbStorage.save(User.builder()
                .email("like@mail.ru")
                .login("like")
                .name("Like")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        filmDbStorage.addLike(film.getId(), user.getId());

        boolean exists = filmDbStorage.isLikeExists(film.getId(), user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    public void testRemoveLike() {

        Film film = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        User user = userDbStorage.save(User.builder()
                .email("u@mail.ru")
                .login("u")
                .name("U")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.removeLike(film.getId(), user.getId());

        boolean exists = filmDbStorage.isLikeExists(film.getId(), user.getId());

        assertThat(exists).isFalse();
    }

    @Test
    public void testGetTopFilmOnLikes() {

        Film film1 = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        Film film2 = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        User user = userDbStorage.save(User.builder()
                .email("top@mail.ru")
                .login("top")
                .name("Top")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        filmDbStorage.addLike(film2.getId(), user.getId());

        List<Film> top = filmDbStorage.getTopFilmOnLikes(1);

        assertThat(top)
                .isNotEmpty()
                .first()
                .extracting(Film::getId)
                .isEqualTo(film2.getId());
    }

    @Test
    public void testDeleteFilm() {

        Film film = filmDbStorage.createFilm(Film.builder()
                .name("New Film")
                .description("Some desc")
                .releaseDate(LocalDate.of(2021, 5, 10))
                .duration(Duration.ofMinutes(120))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .build());

        filmDbStorage.delete(film.getId());

        Optional<Film> deletedFilm = filmDbStorage.findFilmById(film.getId());

        assertThat(deletedFilm).isEmpty();
    }
}
