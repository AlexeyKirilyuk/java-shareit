package ru.practicum.shareit.request.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

@Slf4j
@Component
public class ItemRequestValidation {
    public boolean itemRequestValidation(ItemRequest itemRequest) {
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
