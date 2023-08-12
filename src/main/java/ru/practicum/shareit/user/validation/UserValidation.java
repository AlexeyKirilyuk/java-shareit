package ru.practicum.shareit.user.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserValidation {
    public boolean userCreateValidation(User user) {
        if (user.getEmail() == null) {
            log.debug("Ошибка валидации - электронная почта не может быть пустой");
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой");
        } else if (user.getEmail().isEmpty()) {
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

    public boolean userUpdateValidation(long id, User user, List<User> users) {
        return true;
    }
}
