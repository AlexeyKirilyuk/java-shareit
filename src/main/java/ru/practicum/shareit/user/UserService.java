package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(int id, UserDto userDto);

    public UserDto getUserById(int id);

    public void deleteUserById(int id);

    public List<UserDto> getAllUser();
}
