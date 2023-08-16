package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final LocalDateTime start = LocalDateTime.now().plusMinutes(30);
    private final LocalDateTime end = LocalDateTime.now().plusHours(1);
    private BookingServiceImpl bookingService;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private BookingValidation bookingValidation;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingStorage, userStorage, itemStorage, bookingValidation);
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
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void testAddNew() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
        Long user2Id = user2.getId();
        when(bookingValidation.bookingCreateValidation(anyLong(), any(BookingDto.class), any())).thenReturn(true);
        when(itemStorage.findById(user1.getId())).thenReturn(Optional.of(item1));
        when(userStorage.findById(user2Id)).thenReturn(Optional.of(user2));
        when(bookingStorage.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = invocationOnMock.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                });

        BookingUserDto actualBooking = bookingService.createBooking(user2Id, bookingDto);

        assertThat(actualBooking.getId(), notNullValue());
        assertThat(actualBooking.getStart(), equalTo(start));
        assertThat(actualBooking.getEnd(), equalTo(end));
        assertThat(actualBooking.getItem(), equalTo(ItemMapper.toItemDto(item1)));
        assertThat(actualBooking.getBooker(), equalTo(UserMapper.toUserDto(Optional.ofNullable(user2))));
        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.WAITING));
        Mockito.verify(userStorage, Mockito.times(1)).findById(user2Id);
        Mockito.verify(bookingStorage, Mockito.times(1)).save(Mockito.any(Booking.class));
        Mockito.verifyNoMoreInteractions(itemStorage, userStorage, bookingStorage);
    }

    @Test
    void testAddNewWrongItemId() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(99L)
                .start(start)
                .end(end)
                .build();

        Assertions.assertThrows(AlreadyExistException.class, () -> bookingService.createBooking(user2.getId(), bookingDto));
    }

    @Test
    void testAddNewSameOwner() {
        BookingDto input = BookingDto.builder()
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
        when(itemStorage.findById(item1.getId())).thenReturn(Optional.of(item1));

        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user1.getId(), input));
        Mockito.verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void testAddNewNotAvailItem() {
        item1.setAvailable(false);
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
        Long user2Id = user2.getId();

        Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.createBooking(user2Id, bookingDto));
        Mockito.verify(itemStorage, Mockito.times(1))
                .findById(item1.getId());
        Mockito.verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void testAddNewEndBeforeStart() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start.plusHours(1))
                .end(end)
                .build();
        Long user2Id = user2.getId();

        Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.createBooking(user2Id, bookingDto));
    }

    @Test
    void testAddNewStartSameAsEnd() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start.plusHours(1))
                .end(start.plusHours(1))
                .build();
        Long user2Id = user2.getId();

        Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.createBooking(user2Id, bookingDto));
    }

    @Test
    void testAddNewEndBeforeNow() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start.minusYears(1))
                .end(end.minusYears(1))
                .build();
        Long user2Id = user2.getId();

        Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.createBooking(user2Id, bookingDto));
    }

    @Test
    void testAddNewForOwnItem() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .start(start)
                .end(end)
                .build();
        Long user2Id = user1.getId();

        Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.createBooking(user2Id, bookingDto));

    }

    @Test
    void testConfirmBooking() {
        Long bookingId = booking1.getId();
        boolean approved = true;
        Long itemOwnerId = booking1.getItem().getOwner().getId();
        when(bookingValidation.bookingUpdateValidation(anyLong(), anyLong(), any())).thenReturn(true);
        Mockito
                .when(userStorage.findById(itemOwnerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking1));
        Mockito
                .when(bookingStorage.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        BookingUserDto actualBooking = bookingService.confirmBooking(bookingId, itemOwnerId, approved);

        assertThat(actualBooking.getId(), equalTo(bookingId));
        assertThat(actualBooking.getStatus(), equalTo(BookingStatus.APPROVED));
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findById(bookingId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .save(booking1);

    }

    @Test
    void testConfirmBookingNonValidation() {
        Long bookingId = booking1.getId();
        boolean approved = true;
        Long itemOwnerId = booking1.getItem().getOwner().getId();
        when(bookingValidation.bookingUpdateValidation(anyLong(), anyLong(), any())).thenReturn(false);
        Mockito
                .when(userStorage.findById(itemOwnerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking1));


        ValidationException e = Assertions.assertThrows(ValidationException.class,
                () ->  bookingService.confirmBooking(bookingId, itemOwnerId, approved));

        assertThat(e.getMessage(), equalTo("Ошибка валидации"));
    }

    @Test
    void testGetBookingIsEmpty() {
        booking1.setBooker(user1);
        Long bookingId = booking1.getId();
        Long requesterId = 100L;
        Mockito
                .when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking1));
        Mockito
                .when(userStorage.findById(requesterId))
                .thenReturn(Optional.of(booking1.getBooker()));

        AlreadyExistException e = Assertions.assertThrows(AlreadyExistException.class,
                () ->  bookingService.getBookingById(bookingId, requesterId));

        assertThat(e.getMessage(), equalTo(("Этот пользователь не может одобрить бронирование")));

    }

    @Test
    void testGetBookingyBookingIdSoBig() {
        Long bookingId = 100L;
        Long requesterId = booking1.getBooker().getId();

        Mockito
                .when(userStorage.findById(requesterId))
                .thenReturn(Optional.of(booking1.getBooker()));

        AlreadyExistException e = Assertions.assertThrows(AlreadyExistException.class,
                () ->  bookingService.getBookingById(bookingId, requesterId));

        assertThat(e.getMessage(), equalTo("Бронирование с id = " + bookingId + " не найдено."));

    }

    @Test
    void testGetBooking() {
        Long bookingId = booking1.getId();
        Long requesterId = booking1.getBooker().getId();
        Mockito
                .when(bookingStorage.findById(bookingId))
                .thenReturn(Optional.of(booking1));
        Mockito
                .when(userStorage.findById(requesterId))
                .thenReturn(Optional.of(booking1.getBooker()));

        BookingUserDto actualBooking = bookingService.getBookingById(bookingId, requesterId);

        assertThat(actualBooking.getId(), equalTo(bookingId));
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findById(bookingId);

    }

    @Test
    void testGetAllByBookerIdAndStateDefaultParams() {
        Long bookerId = 1L;
        String defaultState = "ALL";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdOrderByStartDesc(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class));

    }

    @Test
    void testGetAllByBookerIdAndStateALL() {
        Long bookerId = 1L;
        String state = "ALL";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdOrderByStartDesc(
                        Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class));

    }

    @Test
    void testGetAllByBookerIdAndStatePAST() {
        Long bookerId = 1L;
        String state = "PAST";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByBookerIdAndStateCURRENT() {
        Long bookerId = 1L;
        String state = "CuRrEnT";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

    }

    @Test
    void testGetAllByBookerIdAndStateFUTURE() {
        Long bookerId = 1L;
        String state = "FUTURE";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByBookerIdAndStateWAITING() {
        Long bookerId = 1L;
        String state = "WAITING";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(bookerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByBookerIdAndStateWrongState() {
        Long bookerId = 1L;
        String state = "ALLY";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(bookerId))
                .thenReturn(Optional.of(user1));

        IncorrectParameterException e = Assertions.assertThrows(IncorrectParameterException.class,
                () -> bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize));
        assertThat(e.getMessage(), equalTo("Unknown state: " + state.toUpperCase()));
    }

    @Test
    void testGetAllByBookerIdAndStateWrongBookerId() {
        Long bookerId = 99L;
        String state = "ALL";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        AlreadyExistException e = Assertions.assertThrows(AlreadyExistException.class,
                () -> bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize));
        assertThat(e.getMessage(), equalTo("Пользователь с id = " + bookerId + " не найден"));
    }

    @Test
    void testGetAllByBookerIdAndStateWithInvalidPageParams() {
        Long bookerId = 1L;
        String state = "ALL";
        Integer defaultFromElement = 10;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        ValidationException e = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllBookerBookings(bookerId, state, defaultFromElement, defaultSize));
        assertThat(e.getMessage(), equalTo("Некорректный ввод страниц и размеров"));
    }

    @Test
    void testGetAllByOwnerIdAndStateDefaultParams() {
        Long ownerId = 1L;
        String defaultState = "ALL";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByOwnerIdAndStatePast() {
        Long ownerId = 1L;
        String defaultState = "PAST";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByOwnerIdAndStateFuture() {
        Long ownerId = 1L;
        String defaultState = "FUTURE";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByOwnerIdAndStateCurrent() {
        Long ownerId = 1L;
        String defaultState = "CURRENT";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByOwnerIdAndStateWaiting() {
        Long ownerId = 1L;
        String defaultState = "WAITING";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void testGetAllByOwnerIdAndStateRejected() {
        Long ownerId = 1L;
        String defaultState = "REJECTED";
        Integer defaultFromElement = 0;
        Integer defaultSize = 20;
        Mockito
                .when(userStorage.findById(ownerId))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        bookingService.getAllOwnerBookings(ownerId, defaultState, defaultFromElement, defaultSize);

        Mockito.verify(userStorage, Mockito.times(1))
                .findById(ownerId);
        Mockito.verify(bookingStorage, Mockito.times(1))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(userStorage, bookingStorage);
    }

    @Test
    void bookingItemDtoTest() {
        BookingDto input = BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(item1.getId())
                .build();

        Booking between = BookingMapper.fromBookingDtoInput(input, item1, user1, BookingStatus.APPROVED);

        assertThat(between.getStart(), equalTo(input.getStart()));
        assertThat(between.getEnd(), equalTo(input.getEnd()));
        assertThat(between.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(between.getItem().getId(), equalTo(input.getItemId()));

        BookingItemDto betweenDto = BookingMapper.toBookingItemDto(between);

        assertThat(betweenDto.getStart(), equalTo(between.getStart()));
        assertThat(betweenDto.getEnd(), equalTo(between.getEnd()));
        assertThat(betweenDto.getBookerId(), equalTo(user1.getId()));
    }

}