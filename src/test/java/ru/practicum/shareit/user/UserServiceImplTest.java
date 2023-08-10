package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.AlreadyExistException;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@mail.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@mail.ru")
            .build();
    private UserService userService;
    @Mock
    private UserStorage userStorage;
    @Mock
    private UserValidation userValidation;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userStorage, userValidation);
    }

    @Test
    void getAllUsers() {
        List<User> users = Arrays.asList(user1, user2);
        when(userStorage.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAllUser();

        assertEquals(2, userDtos.size());
        verify(userStorage, times(1)).findAll();
    }

    @Test
    void getAllUsersWithEmptyList() {
        when(userStorage.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAllUser();
        Assertions.assertEquals(result, new ArrayList<>());
        verify(userStorage, times(1)).findAll();
    }

    @Test
    void getUserById() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto userDto = userService.getUserById(user1.getId());

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        assertEquals(user1.getEmail(), userDto.getEmail());

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void getUserByIdWithNonExistentUser() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                AlreadyExistException.class, () -> userService.getUserById(10L));

        String expectedMessage = "Ошибка NOT_FOUND";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void saveUser() {
        UserDto userDto = UserMapper.toUserDto(Optional.ofNullable(user1));
        when(userStorage.save(any(User.class))).thenReturn(user1);
        when(userValidation.userCreateValidation(any(User.class), anyList())).thenReturn(true);

        UserDto savedUserDto = userService.createUser(userDto);

        assertEquals(user1.getId(), savedUserDto.getId());
        assertEquals(user1.getName(), savedUserDto.getName());
        assertEquals(user1.getEmail(), savedUserDto.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        UserDto userDto = UserMapper.toUserDto(Optional.ofNullable(user1));
        userDto.setName("updatedName");
        userDto.setEmail("updatedEmail@mail.ru");

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userStorage.save(any())).thenReturn(user1);
        when(userValidation.userUpdateValidation(anyLong(), any(User.class), anyList())).thenReturn(true);

        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertEquals(userDto.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());

        verify(userStorage, times(1)).findById(anyLong());
        verify(userStorage, times(1)).save(any(User.class));
    }

    @Test
    void updateUserWithNonExistentUser() {
        UserDto userDto = UserMapper.toUserDto(Optional.ofNullable(user1));

        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(AlreadyExistException.class, () -> userService.updateUser(10L, userDto));

        String expectedMessage = "Пользователь с id = " + 10L + " не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void updateUserWithBlankName() {
        UserDto userDto = UserDto.builder()
                .name("")
                .email(user2.getEmail())
                .build();

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userStorage.save(user1)).thenReturn(user1);
        when(userValidation.userUpdateValidation(anyLong(), any(User.class), anyList())).thenReturn(true);

        UserDto updatedUserDto = userService.updateUser(user1.getId(), userDto);

        assertEquals(user1.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void updateUserWithExistingEmail() {
        UserDto userDto = UserMapper.toUserDto(Optional.ofNullable(user1));
        userDto.setEmail(user2.getEmail());

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user1));

        Exception exception = assertThrows(
                ValidationException.class, () -> userService.updateUser(user1.getId(), userDto));

        String expectedMessage = "Ошибка валидации";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userStorage, times(1)).findById(anyLong());
    }

    @Test
    void deleteUser() {
        doNothing().when(userStorage).deleteById(anyLong());
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user1));

        userService.deleteUserById(user1.getId());

        verify(userStorage, times(1)).deleteById(anyLong());
    }
}