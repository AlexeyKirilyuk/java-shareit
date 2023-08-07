package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("BookingStorageInMemory")
@RequiredArgsConstructor
public class BookingStorageInMemory /*implements BookingStorage */ {
    /*
    protected final HashMap<Integer, Booking> bookings = new HashMap<>();
    private final BookingValidation bookingValidation;
    protected int idBooking = 0;

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

     */
}