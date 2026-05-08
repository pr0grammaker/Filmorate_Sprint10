import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MapperConfig;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.services.FilmService;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@JdbcTest(properties = "spring.sql.init.data-locations=classpath:data-test.sql")
@AutoConfigureTestDatabase
@ContextConfiguration(classes = FilmorateApplication.class)
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        MapperConfig.class,
        FilmService.class,
        RatingDbStorage.class,
        GenreDbStorage.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {
    private final FilmService filmService;

//----------------------------Обработка исключительных ситуаций

    @Test
    public void testAddFilmAlreadyExist_shouldThrowException() {

        NewFilmRequest film1 = NewFilmRequest.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        filmService.addFilm(film1);

        NewFilmRequest film2 = NewFilmRequest.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        assertThatThrownBy(() -> filmService.addFilm(film2))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testAddFilmEmptyName_shouldThrowException() {

        NewFilmRequest film1 = NewFilmRequest.builder()
                .name("")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        NewFilmRequest film2 = NewFilmRequest.builder()
                .name("       ")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        assertThatThrownBy(() -> filmService.addFilm(film1))
                .isInstanceOf(ConditionsNotMetException.class);

        assertThatThrownBy(() -> filmService.addFilm(film2))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testAddFilmEmptyDescription_shouldThrowException() {

        NewFilmRequest film1 = NewFilmRequest.builder()
                .name("Test Movie1")
                .description("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        NewFilmRequest film2 = NewFilmRequest.builder()
                .name("Test Movie2")
                .description("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();

        assertThatThrownBy(() -> filmService.addFilm(film1))
                .isInstanceOf(ConditionsNotMetException.class);

        assertThatThrownBy(() -> filmService.addFilm(film2))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testAddFilmDescriptionMoreThan200_shouldThrowException() {

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Гамлет")
                .description("""
                        Фильм рассказывает трагическую историю семьи
                        Уильяма Шекспира через призму утраты их сына.
                        Картина показывает, как личная боль и материнская скорбь Агнес
                        Хэтэуэй становятся частью великого художественного наследия.
                        """)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(120)
                .build();

        assertThatThrownBy(() -> filmService.addFilm(film))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testAddFilmInvalidReleaseDate_shouldThrowException() {

        NewFilmRequest film = NewFilmRequest.builder()
                .name("Like Film")
                .description("desc")
                .releaseDate(LocalDate.of(1894, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(90)
                .build();



        assertThatThrownBy(() -> filmService.addFilm(film))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testAddFilmInvalidDuration_shouldThrowException() {

        NewFilmRequest film1 = NewFilmRequest.builder()
                .name("Like Film")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(0)
                .build();

        NewFilmRequest film2 = NewFilmRequest.builder()
                .name("Like Film")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .genres(Set.of(
                        Genre.builder()
                                .id(1L)
                                .name("Экшен")
                                .build()
                ))
                .mpa(
                        Rating.builder()
                                .id(2L)
                                .name("PG")
                                .build()
                )
                .duration(-120)
                .build();

        assertThatThrownBy(() -> filmService.addFilm(film1))
                .isInstanceOf(ConditionsNotMetException.class);

        assertThatThrownBy(() -> filmService.addFilm(film2))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    public void testDeleteFilmNonExistingId_shouldThrowNotFound() {

        assertThatThrownBy(() -> filmService.deleteFilm(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Фильм не найден");
    }

}
