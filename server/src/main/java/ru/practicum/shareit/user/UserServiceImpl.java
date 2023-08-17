package ru.practicum.shareit.user;

import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserValidation userValidation;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (userValidation.userCreateValidation(user)) {
            Optional<User> userOptional = Optional.of(userStorage.save(user));
            return UserMapper.toUserDto(userOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        Optional<User> userDbOptional = userStorage.findById(id);
        if (userDbOptional.isEmpty()) {
            throw new AlreadyExistException("Пользователь с id = " + id + " не найден");
        }  else if (userDto.getEmail() != null) {
            if (!checkUserEmail(userDto.getEmail(), id)) {
                throw new ConflictException("Электронная почта уже занята");
            }
        }
        User userDb = userDbOptional.get();
        if (user.getName() != null) {
            userDb.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userDb.setEmail(user.getEmail());
        }
        Optional<User> userOptional = Optional.of(userStorage.save(userDb));
        return UserMapper.toUserDto(userOptional);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteUserById(Long id) {
        Optional<User> userDb = userStorage.findById(Long.valueOf(id));
        if (userDb.isPresent()) {
            userStorage.deleteById(id);
        } else {
            log.debug("Ошибка NOT_FOUND");
            throw new AlreadyExistException("Ошибка NOT_FOUND");
        }
    }

    @Override
    public List<UserDto> getAllUser() {
        return UserMapper.toListUserDto(userStorage.findAll());
    }

    public boolean checkUserEmail(@Nullable String email, Long id) {
        log.trace("Вызов метода checkUserEmail с email = {}, id = {}", email, id);
        List<User> sameEmailUsers = userStorage.findByEmailContainingIgnoreCase(email);
        if (sameEmailUsers.isEmpty())
            return true;
        for (User user : sameEmailUsers) {
            if (!Objects.equals(user.getId(), id)) {
                return false;
            }
        }
        return true;
    }
}
