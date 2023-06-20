package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class UserValidation {
    public boolean userCreateValidation(User user, HashMap<Integer, User> users) {
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
        } else for (Map.Entry<Integer, User> entry : users.entrySet()) {
            User savedUser = entry.getValue();
            if (savedUser.getEmail().equals(user.getEmail())) {
                log.debug("Ошибка - пользователь с таким Email уже существует.");
                throw new ConflictException("Ошибка - пользователь с таким Email уже существует.");
            }
        }

        return true;
    }

    public boolean userUpdateValidation(User user, HashMap<Integer, User> users) {
        HashMap<Integer, User> userss = new HashMap<>(users);
        userss.remove(user.getId());
        for (Map.Entry<Integer, User> entry : userss.entrySet()) {
            User SavedUser = entry.getValue();
            if (SavedUser.getEmail().equals(user.getEmail())) {
                log.debug("Ошибка - пользователь с таким Email уже существует.");
                throw new ConflictException("Ошибка - пользователь с таким Email уже существует.");
            }
        }

        return true;
    }
}
