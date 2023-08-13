package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.dto.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.dto.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
public class ItemValidationTest {

    private ItemValidation itemValidation;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private final LocalDateTime start = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void beforeEach() {
        itemValidation = new ItemValidation();
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
    void testAddItemWithoutItemName() {
        Long ownerId = 2L;
        item1 = Item.builder()
                .id(1L)
                .name(null)
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        List<UserDto> listUser = UserMapper.toListUserDto(Arrays.asList(user1, user2));

        Exception exception = assertThrows(ValidationException.class,
                () -> itemValidation.itemCreateValidation(item1, ownerId, listUser));

        String expectedMessage = "Ошибка валидации - краткое название не может быть пустым";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddItemWithoutDescription() {
        Long ownerId = 2L;
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description(null)
                .available(true)
                .owner(user1)
                .build();
        List<UserDto> listUser = UserMapper.toListUserDto(Arrays.asList(user1, user2));

        Exception exception = assertThrows(ValidationException.class,
                () -> itemValidation.itemCreateValidation(item1, ownerId, listUser));

        String expectedMessage = "Ошибка валидации - развёрнутое описание вещи не может быть пустым";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddItemWithoutOwnerId() {
        Long ownerId = 0L;
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        List<UserDto> listUser = UserMapper.toListUserDto(Arrays.asList(user1, user2));

        Exception exception = assertThrows(ValidationException.class,
                () -> itemValidation.itemCreateValidation(item1, ownerId, listUser));

        String expectedMessage = "Ошибка валидации - владелец вещи не может быть пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddItemWithoutAvailable() {
        Long ownerId = 2L;
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(null)
                .owner(user1)
                .build();
        List<UserDto> listUser = UserMapper.toListUserDto(Arrays.asList(user1, user2));

        Exception exception = assertThrows(ValidationException.class,
                () -> itemValidation.itemCreateValidation(item1, ownerId, listUser));

        String expectedMessage = "Ошибка валидации - статус доступности вещи не может быть пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testAddItemWrongOwnerId() {
        Long ownerId = 99L;
        item1 = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        List<UserDto> listUser = UserMapper.toListUserDto(Arrays.asList(user1, user2));

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> itemValidation.itemCreateValidation(item1, ownerId, listUser));

        String expectedMessage = "Ошибка валидации - владелец вещи не найден.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateItemWithoutOwnerId() {
        Long ownerId = 0L;
        Long itemId = item1.getId();
        List<Item> listItem = Arrays.asList(item1, item2);

        Exception exception = assertThrows(ValidationException.class,
                () -> itemValidation.itemUpdateValidation(itemId, ownerId, listItem));

        String expectedMessage = "Ошибка валидации - владелец вещи не может быть пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateItemWrongOwnerId() {
        Long ownerId = 99L;
        Long itemId = item1.getId();
        List<Item> listItem = Arrays.asList(item1, item2);

        Exception exception = assertThrows(AlreadyExistException.class,
                () -> itemValidation.itemUpdateValidation(itemId, ownerId, listItem));

        String expectedMessage = "Ошибка - у вещи другой владелец.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
