package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component("BookingStorageInMemory")
@RequiredArgsConstructor
public class BookingStorageInMemory implements BookingStorage {
    private final BookingValidation bookingValidation;
    protected int idBooking = 0;
    protected final HashMap<Integer, Booking> bookings = new HashMap<>();

    public Booking createBooking(Booking booking) {
        if (bookingValidation.bookingValidation(booking)) {
            idBooking++;
            booking.setId(idBooking);
            bookings.put(idBooking, booking);
            log.trace("Добавлено Бронирование " + booking);
        }
        return booking;
    }

    public int updateBooking(Booking booking) {
        if (bookingValidation.bookingValidation(booking)) {
            if (bookings.containsKey(booking.getId())) {
                int id = booking.getId();
                bookings.remove(id);
                bookings.put(id, booking);
                log.trace("Обновлены данные Бронирования " + booking);
            } else {
                log.debug("Ошибка - Бронирование не найдено.");
                throw new AlreadyExistException("Ошибка - Бронирование не найдено.");
            }
        }
        return booking.getId();
    }

    public Booking getBookingById(int id) {
        if (bookings.containsKey(id)) {
            return bookings.get(id);
        } else {
            log.debug("Ошибка - не найден такой id");
            throw new AlreadyExistException("Ошибка - не найден такой id");
        }
    }

    public void deleteBookingById(int id) {
        bookings.remove(id);
    }
}