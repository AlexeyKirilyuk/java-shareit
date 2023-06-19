package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
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

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        System.out.println("user = " + user);
        userStorage.updateUser(user);
        return user;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userStorage.getUserById(id);
    }

    @GetMapping
    public List<User> getUserById() {
        return userStorage.getAllUser();
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUserById(@PathVariable int id) {
        userStorage.deleteUserById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                               //Status code is 400
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                                 //Status code is 404
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                    //Status code is 409
    public Map<String, String> conflictException(final ConflictException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
