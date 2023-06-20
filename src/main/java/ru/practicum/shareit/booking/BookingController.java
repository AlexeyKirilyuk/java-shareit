package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingStorage bookingStorage;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        bookingStorage.createBooking(booking);
        return booking;
    }

    @PutMapping
    public Booking updateBooking(@RequestBody Booking booking) {
        bookingStorage.updateBooking(booking);
        return booking;
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable int id) {
        return bookingStorage.getBookingById(id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteBookingById(@PathVariable int id) {
        bookingStorage.deleteBookingById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
