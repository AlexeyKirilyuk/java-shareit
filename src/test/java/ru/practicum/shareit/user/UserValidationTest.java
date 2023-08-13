package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.dto.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    private UserValidation userValidation;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private final LocalDateTime start = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void beforeEach() {
        userValidation = new UserValidation();
        user1 = User.builder()
                .id(1L)
                .name("User 1 name")
                .email("user1@email.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("User 2 name")
                .email("user2@email.ru")
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user1)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user2)
                .build();
        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(WAITING)
                .build();
    }

    @Test
    void testAddUserWithoutEmail() {
        user1.setEmail(null);

        Exception exception = assertThrows(ValidationException.class,
                () -> userValidation.userCreateValidation(user1));

        String expectedMessage = "Ошибка валидации - электронная почта не может быть пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddUserEmaiIsEmpty() {
        user1.setEmail("");

        Exception exception = assertThrows(ValidationException.class,
                () -> userValidation.userCreateValidation(user1));

        String expectedMessage = "Ошибка валидации - электронная почта не может быть пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddUserEmaiNotContains() {
        user1.setEmail("user11email.ru");

        Exception exception = assertThrows(ValidationException.class,
                () -> userValidation.userCreateValidation(user1));

        String expectedMessage = "Ошибка валидации - электронная почта должна содержать символ @";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddUserNameIsEmpty() {
        user1.setName("");

        userValidation.userCreateValidation(user1);

        assertTrue(user1.getName().equals(user1.getEmail()));
    }

}
