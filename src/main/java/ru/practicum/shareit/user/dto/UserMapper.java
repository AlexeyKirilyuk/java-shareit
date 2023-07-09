package ru.practicum.shareit.user.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMapper {

    public static UserDto toUserDto(Optional<User> user) {
        return new UserDto(
                user.get().getId(),
                user.get().getName(),
                user.get().getEmail()
        );
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
