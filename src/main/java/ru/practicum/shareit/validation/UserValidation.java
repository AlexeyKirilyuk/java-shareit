package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;

import java.util.*;

@Slf4j
@Component
public class UserValidation {
    public boolean userCreateValidation(User user, List<User> users) {
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
        } else for (User savedUser : users) {
            if (savedUser.getEmail().equals(user.getEmail())) {
                log.debug("Ошибка - пользователь с таким Email уже существует.");
                throw new ConflictException("Ошибка - пользователь с таким Email уже существует.");
            }
        }

        return true;
    }

    public boolean userUpdateValidation(int id, User user, List<User> users) {
        List<User> userss = new ArrayList<>(users);
        userss.remove(id);
        for (User savedUser : users) {
            if (savedUser.getEmail().equals(user.getEmail())) {
                log.debug("Ошибка - пользователь с таким Email уже существует.");
                throw new ConflictException("Ошибка - пользователь с таким Email уже существует.");
            }
        }

        return true;
    }
}
