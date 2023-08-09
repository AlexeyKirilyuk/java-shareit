package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.validation.UserValidation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
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
        if (userValidation.userCreateValidation(user, userStorage.findAll())) {
            Optional<User> userOptional = Optional.of(userStorage.save(user));
            return UserMapper.toUserDto(userOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        User userDb = userStorage.findById(id).get();


        if (userValidation.userUpdateValidation(id, user, userStorage.findAll())) {  //* to do
            if (user.getName() != null) {
                userDb.setName(user.getName());
            }
            if (user.getEmail() != null) {
                userDb.setEmail(user.getEmail());
            }

            Optional<User> userOptional = Optional.of(userStorage.save(userDb));
            return UserMapper.toUserDto(userOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userStorage.findById(id));
    }

    @Override
    public void deleteUserById(long id) {
        Optional<User> userDb = userStorage.findById(Long.valueOf(id));
        if (userDb.isPresent()) {
            userStorage.deleteById(id);
            entityManager.flush();
        } else {
            log.debug("Ошибка NOT_FOUND");
            throw new AlreadyExistException("Ошибка NOT_FOUND");
        }


    }


    @Override
    public List<UserDto> getAllUser() {
        return UserMapper.toListUserDto(userStorage.findAll());
    }
}
