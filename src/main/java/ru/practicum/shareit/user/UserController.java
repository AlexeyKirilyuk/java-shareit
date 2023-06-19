package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    @PostMapping
    public User createUser(@RequestBody User user) {
        userStorage.createUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        userStorage.updateUser(user);
        return user;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userStorage.getUserById(id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUserById(@PathVariable int id) {
        userStorage.deleteUserById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
