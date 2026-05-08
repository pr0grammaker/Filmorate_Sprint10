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
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
class FilmoRateApplicationForUserTests {
    private final UserDbStorage userDbStorage;

    @Test
    public void testFindAllUsers() {
        User newUser = User.builder()
                .email("test@mail.ru")
                .login("test_login")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userDbStorage.save(newUser);

        List<User> users = userDbStorage.findAllUsers();

        assertThat(users)
                .isNotEmpty()
                .anySatisfy(user ->
                        assertThat(user).hasFieldOrProperty("id")
                );
    }

    @Test
    public void testGetAllUserFriends() {

        List<User> friends = userDbStorage.getAllUserFriends(1L);

        assertThat(friends)
                .isNotNull()
                .allSatisfy(friend ->
                        assertThat(friend).hasFieldOrProperty("id")
                );
    }

    @Test
    public void testFindByEmail() {
        User newUser = User.builder()
                .email("ivan.petrov@mail.ru")
                .login("test_login")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userDbStorage.save(newUser);

        Optional<User> userOptional =
                userDbStorage.findByEmail("ivan.petrov@mail.ru");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getEmail())
                                .isEqualTo("ivan.petrov@mail.ru")
                );
    }

    @Test
    public void testFindByLogin() {

        User newUser = User.builder()
                .email("test@mail.ru")
                .login("ivan_petrov")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userDbStorage.save(newUser);

        Optional<User> userOptional =
                userDbStorage.findByLogin("ivan_petrov");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getLogin())
                                .isEqualTo("ivan_petrov")
                );
    }

    @Test
    public void testFindUserById() {
        User newUser = User.builder()
                .email("test@mail.ru")
                .login("test_login")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User savedUser = userDbStorage.save(newUser);

        Optional<User> userOptional = userDbStorage.findById(savedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", savedUser.getId())
                );
    }

    @Test
    public void testSaveUser() {

        User newUser = User.builder()
                .email("test@mail.ru")
                .login("test_login")
                .name("Test User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User savedUser = userDbStorage.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@mail.ru");
        assertThat(savedUser.getLogin()).isEqualTo("test_login");
    }

    @Test
    public void testUpdateUser() {

        User user = User.builder()
                .email("old@mail.ru")
                .login("old_login")
                .name("Old Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User saved = userDbStorage.save(user);

        saved.setEmail("new@mail.ru");
        saved.setLogin("new_login");
        saved.setName("New Name");

        User updated = userDbStorage.update(saved);

        assertThat(updated.getEmail()).isEqualTo("new@mail.ru");
        assertThat(updated.getLogin()).isEqualTo("new_login");
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    public void testAddFriend() {

        User user1 = userDbStorage.save(User.builder()
                .email("u1@mail.ru")
                .login("u1")
                .name("U1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User user2 = userDbStorage.save(User.builder()
                .email("u2@mail.ru")
                .login("u2")
                .name("U2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        userDbStorage.addFriend(user1.getId(), user2.getId(), "CONFIRMED");

        List<User> friends = userDbStorage.getAllUserFriends(user1.getId());

        assertThat(friends)
                .extracting(User::getId)
                .contains(user2.getId());
    }

    @Test
    public void testUpdateFriendStatus() {

        User user1 = userDbStorage.save(User.builder()
                .email("a@mail.ru")
                .login("a")
                .name("A")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User user2 = userDbStorage.save(User.builder()
                .email("b@mail.ru")
                .login("b")
                .name("B")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        userDbStorage.addFriend(user1.getId(), user2.getId(), "PENDING");

        userDbStorage.updateFriendStatus(user1.getId(), user2.getId(), "CONFIRMED");

        Optional<String> status =
                userDbStorage.getFriendshipStatus(user1.getId(), user2.getId());

        assertThat(status)
                .isPresent()
                .contains("CONFIRMED");
    }

    @Test
    public void testDeleteUser() {

        User user = userDbStorage.save(User.builder()
                .email("delete@mail.ru")
                .login("delete")
                .name("Delete")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        boolean deleted = userDbStorage.delete(user.getId());

        assertThat(deleted).isTrue();

        Optional<User> found = userDbStorage.findById(user.getId());

        assertThat(found).isEmpty();
    }

    @Test
    public void testGetFriendshipStatus() {

        User user1 = userDbStorage.save(User.builder()
                .email("a@mail.ru")
                .login("a")
                .name("A")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User user2 = userDbStorage.save(User.builder()
                .email("b@mail.ru")
                .login("b")
                .name("B")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        userDbStorage.addFriend(user1.getId(), user2.getId(), "CONFIRMED");

        Optional<String> status =
                userDbStorage.getFriendshipStatus(user1.getId(), user2.getId());

        assertThat(status)
                .isPresent()
                .contains("CONFIRMED");
    }

    @Test
    public void testDeleteFriend() {

        User user1 = userDbStorage.save(User.builder()
                .email("a1@mail.ru")
                .login("a1")
                .name("A1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User user2 = userDbStorage.save(User.builder()
                .email("a2@mail.ru")
                .login("a2")
                .name("A2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        userDbStorage.addFriend(user1.getId(), user2.getId(), "CONFIRMED");

        userDbStorage.deleteFriend(user1.getId(), user2.getId());

        Optional<String> status =
                userDbStorage.getFriendshipStatus(user1.getId(), user2.getId());

        assertThat(status).isEmpty();
    }

    @Test
    public void testGetCommonFriends() {

        User user1 = userDbStorage.save(User.builder()
                .email("u1@mail.ru")
                .login("u1")
                .name("U1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User user2 = userDbStorage.save(User.builder()
                .email("u2@mail.ru")
                .login("u2")
                .name("U2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User commonFriend = userDbStorage.save(User.builder()
                .email("common@mail.ru")
                .login("common")
                .name("Common")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        userDbStorage.addFriend(user1.getId(), commonFriend.getId(), "CONFIRMED");
        userDbStorage.addFriend(user2.getId(), commonFriend.getId(), "CONFIRMED");

        List<User> result =
                userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(result)
                .extracting(User::getId)
                .containsExactly(commonFriend.getId());
    }
}
