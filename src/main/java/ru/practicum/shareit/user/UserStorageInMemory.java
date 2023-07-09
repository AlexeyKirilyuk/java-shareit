package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component("UserStorageInMemory")
@RequiredArgsConstructor
public class UserStorageInMemory /*implements UserStorage */{
    /*
    protected final HashMap<Integer, User> users = new HashMap<>();
    private final UserValidation userValidation;
    protected int idUser = 0;

    public User createUser(User user) {
        idUser++;
        user.setId(idUser);
        users.put(idUser, user);
        log.trace("Добавлен пользователь " + user);
        return user;
    }

    public User updateUser(int id, User user) {
        user.setId(id);
        if (users.containsKey(user.getId())) {
            if (user.getEmail() == null) {
                user.setEmail(users.get(id).getEmail());
            } else if (user.getName() == null) {
                user.setName(users.get(id).getName());
            }
            users.remove(id);
            users.put(id, user);
            log.trace("Обновлены данные пользователя " + user);
        } else {
            log.debug("Ошибка - пользователь не найден.");
            throw new AlreadyExistException("Ошибка - пользователь не найден.");
        }
        return user;
    }

    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.debug("Ошибка - не найден такой id");
            throw new AlreadyExistException("Ошибка - не найден такой id");
        }
    }

    public void deleteUserById(int id) {
        users.remove(id);
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public List<User> getAllUser() {
        return new ArrayList<>(users.values());
    }

     */
}