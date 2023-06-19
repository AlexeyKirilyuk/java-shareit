package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@Slf4j
@Component
public class UserValidation {
    public boolean userValidation(User user) {
        if (user.getEmail().isEmpty() || user.getEmail() == null) {
            log.debug("Ошибка валидации - электронная почта не может быть пустой");
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой");
        } else if (!user.getEmail().contains("@")) {
            log.debug("Ошибка валидации - электронная почта должна содержать символ @");
            throw new ValidationException("Ошибка валидации - электронная почта должна содержать символ @");
        } else if (user.getName() == null || Objects.equals(user.getName(), "")) {
            user.setName(user.getEmail());
        }
        return true;
    }
}
