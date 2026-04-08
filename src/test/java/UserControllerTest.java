import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);

    }

    @Test
    void addUser_validUser_shouldAddSuccessfully() {
        User user = User.builder()
                .email("test@mail.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User added = userService.addUser(user);

        assertNotNull(added.getId());
        assertEquals("test@mail.com", added.getEmail());
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void addUser_emptyEmail_shouldThrowException() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user));
    }

    @Test
    void addUser_isBlankEmail_shouldThrowException() {
        User user = User.builder()
                .email(" user@mail.com   ")
                .login("login")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user));
    }

    @Test
    void addUser_emptyLogin_shouldThrowException() {
        User user = User.builder()
                .email("user@mail.com")
                .login("")
                .name("Name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user));
    }

    @Test
    void updateUser_existingUser_shouldUpdateSuccessfully() {
        User user = User.builder()
                .email("user@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User added = userService.addUser(user);

        User update = User.builder()
                .id(added.getId())
                .email("new@mail.com")
                .login("user101")
                .name("User Updated")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User updated = userService.updateUser(update);

        assertEquals("new@mail.com", updated.getEmail());
        assertEquals("User Updated", updated.getName());
    }

    @Test
    void updateUser_nonExistingId_shouldThrowNotFound() {
        User update = User.builder()
                .id(999L)
                .email("new@mail.com")
                .login("user1")
                .name("User Updated")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        assertThrows(NotFoundException.class, () -> userService.updateUser(update));
    }

    @Test
    void updateUser_nonExistingBirthday_shouldThrowException() {
        User user = User.builder()
                .email("user@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        User added = userService.addUser(user);

        User update = User.builder()
                .id(added.getId())
                .email("new@mail.com")
                .login("user101")
                .name("User Updated")
                .birthday(LocalDate.of(2030, 5, 5))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.updateUser(update));
    }

    @Test
    void addUser_duplicateName_shouldThrowException() {
        User user1 = User.builder()
                .email("user1@mail.com")
                .login("login1")
                .name("John Doe")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        userService.addUser(user1);

        User user2 = User.builder()
                .email("user2@mail.com")
                .login("login2")
                .name("John Doe")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user2));
    }

    @Test
    void addUser_duplicateLogin_shouldThrowException() {
        User user1 = User.builder()
                .email("user1@mail.com")
                .login("login123")
                .name("User One")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        userService.addUser(user1);

        User user2 = User.builder()
                .email("user2@mail.com")
                .login("login123")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user2));
    }

    @Test
    void addUser_duplicateEmail_shouldThrowException() {
        User user1 = User.builder()
                .email("duplicate@mail.com")
                .login("login1")
                .name("User One")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        userService.addUser(user1);

        User user2 = User.builder()
                .email("duplicate@mail.com")
                .login("login2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        assertThrows(ConditionsNotMetException.class, () -> userService.addUser(user2));
    }
}