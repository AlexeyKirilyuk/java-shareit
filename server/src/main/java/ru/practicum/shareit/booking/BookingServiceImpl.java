package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingValidation bookingValidation;

    @Override
    public BookingUserDto createBooking(Long userId, BookingDto bookingDto) {
        checkItem(bookingDto.getItemId());
        Optional<Item> item = itemStorage.findById(bookingDto.getItemId());
        if (bookingValidation.bookingCreateValidation(userId, bookingDto, item.get())) {
            User user = checkUser(userId);
            checkBooking(bookingDto);
            Booking booking = BookingMapper.fromBookingDtoInput(bookingDto, item.get(), user, BookingStatus.WAITING);
            booking = bookingStorage.save(booking);
            return BookingMapper.toBookingUserDto(booking);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    public BookingUserDto confirmBooking(Long bookingId, Long userId, Boolean approved) {
        checkUser(userId);
        Optional<Booking> booking = bookingStorage.findById(bookingId);
        if (bookingValidation.bookingUpdateValidation(bookingId, userId, booking)) {
            booking.get().setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            Booking result = bookingStorage.save(booking.get());
            return BookingMapper.toBookingUserDto(result);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    public BookingUserDto getBookingById(Long bookingId, Long userId) {
        checkUser(userId);
        Optional<Booking> booking = bookingStorage.findById(bookingId);
        if (booking.isEmpty()) {
            String e = "Бронирование с id = " + bookingId + " не найдено.";
            log.debug(e);
            throw new AlreadyExistException(e);
        } else {
            if (!booking.get().getBooker().getId().equals(userId)
                    && !booking.get().getItem().getOwner().getId().equals(userId)) {
                String e = "Этот пользователь не может одобрить бронирование";
                log.debug(e);
                throw new AlreadyExistException(e);
            }
            return BookingMapper.toBookingUserDto(booking.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingUserDto> getAllOwnerBookings(Long ownerId, String state, Integer fromElement, Integer size) {
        checkUser(ownerId);
        List<Booking> result;
        checkPages(fromElement, size);
        int fromPage = fromElement / size;
        Pageable pageable = PageRequest.of(fromPage, size);
        try {
            BookingState status = BookingState.valueOf(state);
            switch (status) {
                case ALL:
                    result = bookingStorage.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                    break;
                case PAST:
                    result = bookingStorage.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                    break;
                case FUTURE:
                    result = bookingStorage.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable);
                    break;
                case CURRENT:
                    result = bookingStorage.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                    break;
                case WAITING:
                    result = bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
                    break;
                case REJECTED:
                    result = bookingStorage.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
                    break;
                default:
                    throw new IncorrectParameterException(state);
            }
            return BookingMapper.fromListBooking(result);
        } catch (Exception e) {
            throw new IncorrectParameterException(state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingUserDto> getAllBookerBookings(Long bookerId, String state, Integer fromElement, Integer size) {
        checkUser(bookerId);
        List<Booking> result;
        checkPages(fromElement, size);
        int fromPage = fromElement / size;
        Pageable pageable = PageRequest.of(fromPage, size);
        try {
            BookingState status = BookingState.valueOf(state.toUpperCase());
        switch (status) {
            case ALL:
                result = bookingStorage.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
                break;
            case PAST:
                result = bookingStorage.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                result = bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                result = bookingStorage.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case WAITING:
                result = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new IncorrectParameterException(String.format("Unknown state: %s", state.toUpperCase()));
        }
        return BookingMapper.fromListBooking(result);
        } catch (Exception e) {
            throw new IncorrectParameterException(String.format("Unknown state: %s", state.toUpperCase()));
        }
    }

    public User checkUser(Long userId) {
        return userStorage.findById(userId)
                          .orElseThrow(() -> new AlreadyExistException("Пользователь с id = " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        log.trace("Вызов метода checkItem с itemId = {}", itemId);
        Optional<Item> item = itemStorage.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new AlreadyExistException("Предмет с id = " + itemId + " не найден");
        }
    }

    public void checkBooking(BookingDto booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка во времени бронирования: " +
                    "оно должно закончиться в будущем времени.");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Ошибка во времени бронирования: " +
                    "конец бронирования должен быть после его начала.");
        }
        if (booking.getEnd().isEqual(booking.getStart())) {
            throw new ValidationException("Ошибка во времени бронирования: " +
                    "время начала не может совпадать с временем окончания. ");
        }
    }

    public void checkPages(Integer fromElement, Integer size) {
        if (fromElement % size != 0) {
            throw new ValidationException("Некорректный ввод страниц и размеров");
        }
    }

}
