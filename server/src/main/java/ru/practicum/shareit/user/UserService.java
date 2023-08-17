package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto createUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto userDto);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    public List<UserDto> getAllUser();
}
