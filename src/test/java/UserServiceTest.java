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
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MapperConfig;
import ru.yandex.practicum.filmorate.services.UserService;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@JdbcTest(properties = "spring.sql.init.data-locations=classpath:data-test.sql")
@AutoConfigureTestDatabase
@ContextConfiguration(classes = FilmorateApplication.class)
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        MapperConfig.class,
        UserService.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;

    @Test
    public void testAddFriendSelf_shouldThrowException() {

        NewUserRequest user = NewUserRequest.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        long id = userService.createUser(user).getId();

        assertThatThrownBy(() -> userService.addFriend(id, id))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void addFriend_shouldThrow_whenAlreadyFriend() {

        NewUserRequest user1 = NewUserRequest.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        NewUserRequest user2 = NewUserRequest.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build();

        long id1 = userService.createUser(user1).getId();
        long id2 = userService.createUser(user2).getId();

        userService.addFriend(id1, id2);

        assertThatThrownBy(() -> userService.addFriend(id1, id2))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void deleteFriend_shouldThrow_whenFriendIdIsNull() {

        NewUserRequest user = NewUserRequest.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        long id = userService.createUser(user).getId();

        assertThatThrownBy(() -> userService.deleteFriend(id, 0))
                .isInstanceOf(NotFoundException.class);
    }


    @Test
    void getFriends_shouldThrow_whenUserNotFound() {

        assertThatThrownBy(() -> userService.getAllFriends(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getCommonFriends_shouldThrow_whenUserNotFound() {

        long id = userService.createUser(NewUserRequest.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build()).getId();

        assertThatThrownBy(() ->
                userService.getGeneralListFriendsWithAFriend(id, 999L))
                .isInstanceOf(NotFoundException.class);
    }
}
