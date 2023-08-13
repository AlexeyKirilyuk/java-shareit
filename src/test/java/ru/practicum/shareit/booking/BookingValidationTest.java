package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.dto.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.dto.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class BookingValidationTest {

    private BookingValidation bookingValidation;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;
    private final LocalDateTime start = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void beforeEach() {
        bookingValidation = new BookingValidation();
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
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
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
    void testAddNewBookingWithoutItem() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        Optional<Item> item = null;

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Предмет не найден(isEmpty)";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddNewBookingWithoutItemId() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(null)
                .start(start)
                .end(end)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Предмет с этим id не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddNewBookingAvailableFalse() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .owner(user1)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Статус данной вещи недоступен.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddNewBookingUserIdEqualsOverId() {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Пользователь не может арендовать свою же вещь.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddNewBookingStartIsNull() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(null)
                .end(end)
                .build();

        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Ошибка валидации - время начала не может быть Null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void testAddNewBookingEndIsNull() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(null)
                .build();

        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingValidation.bookingCreateValidation(userId, bookingDto, item));

        String expectedMessage = "Ошибка валидации - время начала не может быть Null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddNewBookingIsValidation() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        Optional<Item> item = Optional.ofNullable(item1);

        boolean validation = bookingValidation.bookingCreateValidation(userId, bookingDto, item);

        assertTrue(validation);
    }

    @Test
    void testUpdateBookingAvailableFalse() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .owner(user2)
                .build();
        booking1 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user1)
                .status(APPROVED)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(ValidationException.class,
                () -> bookingValidation.bookingUpdateValidation(booking1.getId(), userId, Optional.ofNullable(booking1)));

        String expectedMessage = "Вещь уже забронирована.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateBookingOvnerFols() {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(false)
                .owner(user2)
                .build();
        booking1 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user1)
                .status(APPROVED)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> bookingValidation.bookingUpdateValidation(booking1.getId(), userId, Optional.ofNullable(booking1)));

        String expectedMessage = "Пользователь с id = 1 не является владельцем бронирование";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateBookingIsValidation() {
        Long userId = 2L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user2)
                .build();
        booking1 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user1)
                .status(WAITING)
                .build();
        Optional<Item> item = Optional.ofNullable(item1);

        boolean validation = bookingValidation.bookingUpdateValidation(booking1.getId(), userId, Optional.ofNullable(booking1));

        assertTrue(validation);
    }
}
