package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.validation.UserValidation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UserStorage userStorage;
    private final UserValidation userValidation;
    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userValidation.userCreateValidation(user, userStorage.findAll())) {
            Optional<User> userOptional = Optional.of(userStorage.save(user));
            return UserMapper.toUserDto(userOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }
    @Override
    @Transactional
    public UserDto updateUser(int id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        if (userValidation.userUpdateValidation(id, user, userStorage.findAll())) {
            Optional<User> userOptional = Optional.of(userStorage.save(user));
            return UserMapper.toUserDto(userOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }
    @Override
    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(userStorage.findById((long) id));
    }
    @Override
    public void deleteUserById(int id) {
        userStorage.deleteById((long) id);
    }
    @Override
    public List<UserDto> getAllUser() {
        return UserMapper.toListUserDto(userStorage.findAll());
    }
}
