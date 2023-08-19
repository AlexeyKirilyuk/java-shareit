package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("*************************************************************************************************************************");
        log.info("           Добавление пользователя  = {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,@RequestBody UserDto userDto) {
        log.info("*************************************************************************************************************************");
        log.info("           Обновление пользователя с Id = {}", userId);
        log.info("           UserDto = {}", userDto);
        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("*************************************************************************************************************************");
        log.info("           Получение пользователя с Id = {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUser() {
        log.info("*************************************************************************************************************************");
        log.info("           Получение всех пользователей");
        return userService.getAllUser();
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("*************************************************************************************************************************");
        log.info("           Удаление пользователя с Id = {}", userId);
        userService.deleteUserById(userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                               //Status code is 400
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации","errorMessage", e.getMessage());
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
