package ru.practicum.shareit.booking.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.Item;

import java.util.Optional;

@Slf4j
@Component
public class BookingValidation {
    public boolean bookingCreateValidation(Long userId, BookingDto bookingDto, ItemStorage itemStorage) {
        if (bookingDto.getItemId() == null) {
            String e = "Предмет с id = " + bookingDto.getItemId() + " не найден";
            log.debug(e);
            throw new AlreadyExistException(e);
        }
        Optional<Item> item = itemStorage.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            String e = "Предмет с id = " + bookingDto.getItemId() + " не найден(isEmpty)";
            log.debug(e);
            throw new AlreadyExistException(e);
        } else if (!item.get().getAvailable()) {
            String e = "Статус данной вещи недоступен.";
            log.debug(e);
            throw new ValidationException(e);
        } else if (item.get().getOwner().getId().equals(userId)) {
            String e = "Пользователь не может арендовать свою же вещь.";
            log.debug(e);
            throw new AlreadyExistException(e);
        } else if (bookingDto.getStart() == null) {
            String e = "Ошибка валидации - время начала не может быть Null";
            log.debug(e);
            throw new ValidationException(e);
        } else if (bookingDto.getEnd() == null) {
            String e = "Ошибка валидации - время начала не может быть Null";
            log.debug(e);
            throw new ValidationException(e);
        }
        return true;
    }

    public boolean bookingUpdateValidation(Long bookingId, Long userId, Boolean approved, Optional<Booking> booking) {
        if (booking.isEmpty()) {
            String e = "Бронирование с id = " + bookingId + " не найдено.";
            log.debug(e);
            throw new ValidationException(e);
        } else if (booking.get().getItem().getOwner().getId().equals(userId)
                && booking.get().getStatus().equals(BookingStatus.APPROVED)) {
            String e = "Вещь уже забронирована.";
            log.debug(e);
            throw new ValidationException(e);
        }
        if (!booking.get().getItem().getOwner().getId().equals(userId)) {
            String e = "Пользователь с id = " + userId + " не является владельцем бронирование";
            log.debug(e);
            throw new AlreadyExistException(e);
        }
        return true;
    }
}