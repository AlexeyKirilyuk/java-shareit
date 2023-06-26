package ru.practicum.shareit.user.dto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
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
            listUserDto.add(toUserDto(user));
        }
        return listUserDto;
    }
}
