package ru.practicum.shareit.booking.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@Component
public class BookingValidation {
    public boolean bookingValidation(Booking booking) {
    /*
        if (user.getEmail().isEmpty() || user.getEmail() == null) {
            log.debug("Ошибка валидации - электронная почта не может быть пустой");
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой");
        } else if (!user.getEmail().contains("@")) {
            log.debug("Ошибка валидации - электронная почта должна содержать символ @");
            throw new ValidationException("Ошибка валидации - электронная почта должна содержать символ @");
        } else if (user.getName() == null || Objects.equals(user.getName(), "")) {
            user.setName(user.getEmail());
        }
    */
        return true;

    }

}
