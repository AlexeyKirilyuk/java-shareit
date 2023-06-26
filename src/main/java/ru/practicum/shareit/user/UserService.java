package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.UserValidation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserValidation userValidation;

    public UserDto createUser(User user) {
        if (userValidation.userCreateValidation(user, userStorage.getUsers())) {
            return UserMapper.toUserDto(userStorage.createUser(user));
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    public UserDto updateUser(int id, User user) {
        if (userValidation.userUpdateValidation(id, user, userStorage.getUsers())) {
            return UserMapper.toUserDto(userStorage.updateUser(id, user));
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }

    public List<UserDto> getAllUser() {
        return UserMapper.toListUserDto(userStorage.getAllUser());
    }
}
