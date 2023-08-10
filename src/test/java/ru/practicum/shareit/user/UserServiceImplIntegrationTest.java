package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();
        user2 = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();
    }

    @Test
    void updateUser() {
        userStorage.save(user1);
        userStorage.save(user2);

        String newName = "updatedUser1";
        String newEmail = "updatedUser1@mail.ru";

        UserDto userDto = UserDto.builder()
                .name("updatedUser1")
                .email("updatedUser1@mail.ru")
                .build();

        UserDto updatedUser = userService.updateUser(user1.getId(), userDto);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newEmail, updatedUser.getEmail());

        UserDto nonUpdatedUser = userService.getUserById(user2.getId());

        assertNotNull(nonUpdatedUser);
        assertNotEquals(newName, nonUpdatedUser.getName());
        assertNotEquals(newEmail, nonUpdatedUser.getEmail());
    }
}