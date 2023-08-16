package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private UserDto userDto1;
    private ItemDto itemDto1;
    private ItemDto itemDto2;

    @BeforeEach
    void setUp() {
        userDto1 = userService.createUser(UserDto.builder()
                .name("User 1 name")
                .email("user1@email.com")
                .build());
        UserDto userDto2 = userService.createUser(UserDto.builder()
                .name("User 2 name")
                .email("user2@email.com")
                .build());
        itemDto1 = itemService.createItem(
                ItemDto.builder()
                        .name("Item 1 name")
                        .description("Item 1 description")
                        .available(true)
                        .build(),
                userDto1.getId()
        );
        itemDto2 = itemService.createItem(
                ItemDto.builder()
                        .name("Item 2 name")
                        .description("Item 2 description")
                        .available(false)
                        .build(),
                userDto1.getId()
        );
    }

    @Test
    void testGetAllOwnerItems() {
        Long userId = userDto1.getId();
        List<ItemDto> actualItems = itemService.getItemByOwner(userId);

        assertThat(actualItems.size(), equalTo(2));
        assertThat(actualItems.get(0).getId(), equalTo(itemDto1.getId()));
        assertThat(actualItems.get(0).getName(), equalTo("Item 1 name"));
        assertThat(actualItems.get(0).getAvailable(), equalTo(true));
        assertThat(actualItems.get(1).getId(), equalTo(itemDto2.getId()));
        assertThat(actualItems.get(1).getName(), equalTo("Item 2 name"));
        assertThat(actualItems.get(1).getAvailable(), equalTo(false));
    }
}