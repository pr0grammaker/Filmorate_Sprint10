import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    private User createUser() {
        return userStorage.addUser(User.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());
    }

    private Film createFilm() {
        return filmStorage.addFilm(Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build());
    }

    @Test
    void likeTheFilm_shouldAddSuccessfully() {
        User user = createUser();
        Film film = createFilm();

        Film updated = filmService.likeTheFilm(film.getId(), user.getId());

        assertTrue(updated.getLikes().contains(user.getId()));
    }

    @Test
    void likeTheFilm_shouldThrow_whenUserNotFound() {
        Film film = createFilm();

        assertThrows(NotFoundException.class,
                () -> filmService.likeTheFilm(film.getId(), 999L));
    }

    @Test
    void likeTheFilm_shouldThrow_whenFilmNotFound() {
        User user = createUser();

        assertThrows(NotFoundException.class,
                () -> filmService.likeTheFilm(999L, user.getId()));
    }

    @Test
    void likeTheFilm_shouldThrow_whenAlreadyLiked() {
        User user = createUser();
        Film film = createFilm();

        filmService.likeTheFilm(film.getId(), user.getId());

        assertThrows(ConditionsNotMetException.class,
                () -> filmService.likeTheFilm(film.getId(), user.getId()));
    }

    @Test
    void deleteLikeTheFilm_shouldRemoveSuccessfully() {
        User user = createUser();
        Film film = createFilm();

        filmService.likeTheFilm(film.getId(), user.getId());
        Film updated = filmService.deleteLikeTheFilm(film.getId(), user.getId());

        assertFalse(updated.getLikes().contains(user.getId()));
    }

    @Test
    void deleteLikeTheFilm_shouldThrow_whenLikeNotExists() {
        User user = createUser();
        Film film = createFilm();

        assertThrows(ConditionsNotMetException.class,
                () -> filmService.deleteLikeTheFilm(film.getId(), user.getId()));
    }

    @Test
    void deleteLikeTheFilm_shouldThrow_whenUserNotFound() {
        Film film = createFilm();

        assertThrows(NotFoundException.class,
                () -> filmService.deleteLikeTheFilm(film.getId(), 999L));
    }

    @Test
    void getTopFilms_shouldReturnSortedByLikes() {
        User user1 = createUser();
        User user2 = userStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1991, 1, 1))
                .build());

        Film film1 = createFilm();
        Film film2 = filmStorage.addFilm(Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build());

        // film1 = 2 лайка
        filmService.likeTheFilm(film1.getId(), user1.getId());
        filmService.likeTheFilm(film1.getId(), user2.getId());

        // film2 = 1 лайк
        filmService.likeTheFilm(film2.getId(), user1.getId());

        Collection<Film> top = filmService.getTopFilmOnLikes(10);

        Film first = top.iterator().next();
        assertEquals(film1.getId(), first.getId());
    }

    @Test
    void getTopFilms_shouldRespectLimit() {
        User user = createUser();

        Film film1 = createFilm();
        Film film2 = filmStorage.addFilm(Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build());

        filmService.likeTheFilm(film1.getId(), user.getId());
        filmService.likeTheFilm(film2.getId(), user.getId());

        Collection<Film> top = filmService.getTopFilmOnLikes(1);

        assertEquals(1, top.size());
    }

    @Test
    void getTopFilms_shouldReturnEmpty_whenNoFilms() {
        Collection<Film> top = filmService.getTopFilmOnLikes(10);

        assertTrue(top.isEmpty());
    }
}