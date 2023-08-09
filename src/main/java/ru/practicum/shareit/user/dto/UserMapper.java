package ru.practicum.shareit.user.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.AlreadyExistException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserMapper {

    public static UserDto toUserDto(Optional<User> user) {
        if (user.isPresent()) {
            return new UserDto(
                    user.get().getId(),
                    user.get().getName(),
                    user.get().getEmail()
            );
        }
        log.debug("Ошибка NOT_FOUND");
        throw new AlreadyExistException("Ошибка NOT_FOUND");
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static List<UserDto> toListUserDto(List<User> listUser) {
        List<UserDto> listUserDto = new ArrayList<>();
        for (User user : listUser) {
            Optional<User> userOptional = Optional.of(user);
            listUserDto.add(toUserDto(userOptional));
        }
        return listUserDto;
    }
}
