import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void addFilm_validFilm_shouldAddSuccessfully() {
        Film film = Film.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        Film added = filmController.addMovie(film);

        assertNotNull(added.getId());
        assertEquals("Test Movie", added.getName());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_alreadyExistFilm_shouldThrowException() {
        Film film1 = Film.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        filmController.addMovie(film1);

        Film film2 = Film.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmController.addMovie(film2));
    }

    @Test
    void addFilm_emptyName_shouldThrowException() {
        Film film = Film.builder()
                .name("")
                .description("Some description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmController.addMovie(film));
    }

    @Test
    void addFilm_emptyDescription_shouldThrowException() {
        Film film = Film.builder()
                .name("Melody")
                .description("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmController.addMovie(film));
    }

    @Test
    void addFilm_lengthDescriptionMore200_shouldThrowException() {
        Film film = Film.builder()
                .name("Гамнет")
                .description("""
                        Фильм рассказывает трагическую историю семьи
                        Уильяма Шекспира через призму утраты их сына.
                        Картина показывает, как личная боль и материнская скорбь Агнес
                        Хэтэуэй становятся частью великого художественного наследия.
                        """)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmController.addMovie(film));
    }

    @Test
    void updateFilm_existingFilm_shouldUpdateSuccessfully() {
        Film film = Film.builder()
                .name("Old Movie")
                .description("Old description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(100))
                .build();

        Film added = filmController.addMovie(film);

        Film update = Film.builder()
                .id(added.getId())
                .name("Updated Movie")
                .description("Updated description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(Duration.ofMinutes(110))
                .build();

        Film updated = filmController.updateMovie(update);

        assertEquals("Updated Movie", updated.getName());
        assertEquals(110, updated.getDuration().toMinutes());
    }

    @Test
    void updateFilm_nonExistingId_shouldThrowNotFound() {
        Film update = Film.builder()
                .id(999L)
                .name("Updated Movie")
                .description("Updated description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(Duration.ofMinutes(110))
                .build();

        assertThrows(NotFoundException.class, () -> filmController.updateMovie(update));
    }

    @Test
    void updateFilm_notValidReariseDate_shouldThrowException() {
        Film film1 = Film.builder()
                .name("Test Movie")
                .description("Some description")
                .releaseDate(LocalDate.of(1894, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> filmController.addMovie(film1));
    }
}