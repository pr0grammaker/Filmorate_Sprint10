import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.AnyOtherException;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
    }


    @Test
    void addFriend_shouldAddFriendSuccessfully() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        User updated = userService.addFriend(user1.getId(), user2.getId());

        assertTrue(updated.getFriends().contains(user2.getId()));
    }

    @Test
    void addFriend_shouldThrow_whenAddSelf() {
        User user = inMemoryUserStorage.addUser(User.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        assertThrows(AnyOtherException.class,
                () -> userService.addFriend(user.getId(), user.getId()));
    }

    @Test
    void addFriend_shouldThrow_whenAlreadyFriend() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        userService.addFriend(user1.getId(), user2.getId());

        assertThrows(ConditionsNotMetException.class,
                () -> userService.addFriend(user1.getId(), user2.getId()));
    }

    @Test
    void deleteFriend_shouldRemoveFriendSuccessfully() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        userService.addFriend(user1.getId(), user2.getId());
        User updated = userService.deleteFriend(user1.getId(), user2.getId());

        assertFalse(updated.getFriends().contains(user2.getId()));
    }

    @Test
    void deleteFriend_shouldThrow_whenFriendNotExists() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        assertThrows(NotFoundException.class,
                () -> userService.deleteFriend(user1.getId(), user2.getId()));
    }

    @Test
    void deleteFriend_shouldThrow_whenFriendIdIsNull() {
        User user = inMemoryUserStorage.addUser(User.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        assertThrows(ConditionsNotMetException.class,
                () -> userService.deleteFriend(user.getId(), null));
    }

    @Test
    void getFriends_shouldReturnFriendsList() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        userService.addFriend(user1.getId(), user2.getId());

        Set<Long> friends = userService.getFriendsForTest(user1.getId());

        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2.getId()));
    }

    @Test
    void getCommonFriends_shouldReturnIntersection() {
        User user1 = inMemoryUserStorage.addUser(User.builder()
                .email("user1@mail.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        User user2 = inMemoryUserStorage.addUser(User.builder()
                .email("user2@mail.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1992, 2, 2))
                .build());

        User user3 = inMemoryUserStorage.addUser(User.builder()
                .email("user3@mail.com")
                .login("user3")
                .name("User Three")
                .birthday(LocalDate.of(1993, 3, 3))
                .build());

        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        Set<Long> commonFriends =
                userService.getGeneralListFriendsWithAFriendForTest(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user3.getId()));
    }

    @Test
    void getFriends_shouldThrow_whenUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> userService.getFriendsForTest(999L));
    }

    @Test
    void getCommonFriends_shouldThrow_whenUserNotFound() {
        User user = inMemoryUserStorage.addUser(User.builder()
                .email("user@mail.com")
                .login("user")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build());

        assertThrows(NotFoundException.class,
                () -> userService.getGeneralListFriendsWithAFriendForTest(user.getId(), 999L));
    }
}
