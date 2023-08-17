package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking fromBookingDtoInput(BookingDto bookingDto, Item item, User user, BookingStatus status) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            throw new IncorrectParameterException("Время не может быть null");
        }
        if (start.isAfter(end) || start.isEqual(end) || start.isBefore(LocalDateTime.now())) {
            throw new IncorrectParameterException("Некорректное время");
        }

        return Booking.builder()
                .id(null)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public static BookingUserDto toBookingUserDto(Booking booking) {
        return BookingUserDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toItemDto((booking.getItem())))
                .booker(UserMapper.toUserDto(Optional.ofNullable(booking.getBooker())))
                .build();
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (booking == null)
            return null;
        else return BookingItemDto.builder()
                .bookerId(booking.getBooker().getId())
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }


    public static List<BookingUserDto> fromListBooking(List<Booking> input) {
        return input.stream()
                .map(BookingMapper::toBookingUserDto).collect(Collectors.toList());
    }
}
