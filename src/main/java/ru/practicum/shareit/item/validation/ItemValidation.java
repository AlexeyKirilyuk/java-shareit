package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Slf4j
@Component
public class ItemValidation {
    public boolean itemValidation(Item item) {
        if (item.getName() == null || Objects.equals(item.getName(), "")) {
            log.debug("Ошибка валидации - краткое название не может быть пустым");
            throw new ValidationException("Ошибка валидации - краткое название не может быть пустым");
        }
        return true;
    }
}
