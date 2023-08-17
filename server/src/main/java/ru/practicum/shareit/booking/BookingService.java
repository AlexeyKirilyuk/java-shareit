package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUserDto;

import java.util.List;

public interface BookingService {

    BookingUserDto createBooking(Long userId, BookingDto bookingDto);

    BookingUserDto confirmBooking(Long bookingId, Long userId, Boolean approved);

    BookingUserDto getBookingById(Long bookingId, Long userId);

    List<BookingUserDto> getAllOwnerBookings(Long ownerId, String state, Integer fromElement, Integer size);

    List<BookingUserDto> getAllBookerBookings(Long userId, String state, Integer fromElement, Integer size);

}
