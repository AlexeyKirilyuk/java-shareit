package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.comments.CommentStorage;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private ItemServiceImpl itemService;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private ItemValidation itemValidation;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @Mock
    private UserServiceImpl userService;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemStorage, itemValidation, userStorage, itemRequestStorage, userService, commentStorage, bookingStorage);
        user1 = User.builder()
                .id(1L)
                .name("User_1 name")
                .email("user_1@email.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("User_2 name")
                .email("user_2@email.ru")
                .build();
        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest description")
                .requestor(user1)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("Item 1 name")
                .description("Item 1 description")
                .available(true)
                .owner(user1)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Item 2 name")
                .description("Item 2 description")
                .available(true)
                .owner(user2)
                .request(itemRequest1)
                .build();
    }

    @Test
    void testAddNew() {
        Long newItemId = 5L;
        ItemDto createItemDto = ItemMapper.toItemDto(item1);
        Long userId = item1.getOwner().getId();

        when(itemValidation.itemCreateValidation(any(Item.class), anyLong(), anyList())).thenReturn(true);
        when(userStorage.findById(userId)).thenReturn(Optional.of(item1.getOwner()));
        when(itemStorage.save(Mockito.any(Item.class))).thenAnswer(
                invocationOnMock -> {
                    Item item = invocationOnMock.getArgument(0, Item.class);
                    item.setId(newItemId);
                    return item;
                });

        ItemDto actualItemDto = itemService.createItem(createItemDto, userId);

        assertThat(actualItemDto.getId(), equalTo(newItemId));
        assertThat(actualItemDto.getName(), equalTo(item1.getName()));
        assertThat(actualItemDto.getRequestId(), equalTo(null));
        verify(userStorage, times(1))
                .findById(userId);
        verify(itemStorage, times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void testAddNewToItemRequest() {
        Long newItemId = 5L;
        ItemDto createItemDto = ItemMapper.toItemDto(item2);
        Long userId = item2.getOwner().getId();
        when(itemValidation.itemCreateValidation(any(Item.class), anyLong(), anyList())).thenReturn(true);
        when(userStorage.findById(userId)).thenReturn(Optional.of(item2.getOwner()));
        when(itemRequestStorage.findById(createItemDto.getRequestId())).thenReturn(Optional.of(itemRequest1));
        when(itemStorage.save(Mockito.any(Item.class))).thenAnswer(
                invocationOnMock -> {
                    Item item = invocationOnMock.getArgument(0, Item.class);
                    item.setId(newItemId);
                    return item;
                });

        ItemDto actualItemDto = itemService.createItem(createItemDto, userId);

        assertThat(actualItemDto.getId(), equalTo(newItemId));
        assertThat(actualItemDto.getName(), equalTo(item2.getName()));
        assertThat(actualItemDto.getRequestId(), equalTo(item2.getRequest().getId()));
        verify(userStorage, times(1))
                .findById(userId);
        verify(itemRequestStorage, times(1))
                .findById(createItemDto.getRequestId());
        verify(itemStorage, times(1))
                .save(Mockito.any(Item.class));
    }

    @Test
    void testPatchUpdate() {
        String newUserName = "New user name";
        String expectedDesc = item1.getDescription();
        Long itemId = item1.getId();
        Long userId = item1.getOwner().getId();
        ItemDto itemDto = ItemDto.builder()
                .name(newUserName)
                .description(null)
                .available(false)
                .build();
        when(itemValidation.itemUpdateValidation(anyLong(), anyLong(), anyList())).thenReturn(true);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item1));
        when(itemStorage.save(item1)).thenReturn(item1);
        when(userStorage.findById(userId)).thenReturn(Optional.of(item1.getOwner()));

        ItemDto actualItemDto = itemService.updateItem(userId, itemDto, itemId);

        assertThat(actualItemDto.getId(), equalTo(itemId));
        assertThat(actualItemDto.getName(), equalTo(newUserName));
        assertThat(actualItemDto.getDescription(), equalTo(expectedDesc));
        verify(itemStorage, times(1))
                .findById(itemId);
        verify(itemStorage, times(1))
                .save(item1);
    }

    @Test
    void testPatchUpdateWrongItemId() {
        String newUserName = "New user name";
        Long itemId = 99L;
        Long userId = item1.getOwner().getId();
        ItemDto itemDto = ItemDto.builder()
                .name(newUserName)
                .description(null)
                .available(false)
                .build();

        ValidationException e = assertThrows(ValidationException.class,
                () -> itemService.updateItem(userId, itemDto, itemId));
        assertThat(e.getMessage(), equalTo("Ошибка валидации"));
    }

    @Test
    void testGetByIdWithoutCommentsAndWithBookingGettingByNoOwner() {
        Long itemId = item1.getId();
        Long userId = user2.getId();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item1));
        when(commentStorage.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemDto actualItemDto = itemService.getItemById(userId, itemId);

        assertThat(actualItemDto.getId(), equalTo(itemId));
        assertThat(actualItemDto.getName(), equalTo(item1.getName()));
        assertThat(actualItemDto.getLastBooking(), equalTo(null));
        assertThat(actualItemDto.getNextBooking(), equalTo(null));
        assertThat(actualItemDto.getComments().size(), equalTo(0));
        verify(itemStorage, times(1))
                .findById(itemId);
        verify(commentStorage, times(1))
                .findAllByItemId(itemId);
        Mockito.verifyNoMoreInteractions(itemStorage, commentStorage);
    }

    @Test
    void testAddNewComment_whenValid() {
        Long itemId = item1.getId();
        Long userId = user2.getId();
        CommentDto requestComment = CommentDto.builder()
                .text("text")
                .build();
        Comment expectedComment = Comment.builder()
                .id(1L)
                .text(requestComment.getText())
                .item(item1)
                .authorName(user2)
                .created(LocalDateTime.now().minusHours(1))
                .build();
        when(bookingStorage.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(true);
        when(itemStorage.findById(itemId))
                .thenReturn(Optional.of(item1));
        when(userStorage.findById(userId))
                .thenReturn(Optional.of(user2));
        when(commentStorage.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    Comment comment = invocationOnMock.getArgument(0, Comment.class);
                    comment.setId(expectedComment.getId());
                    comment.setCreated(expectedComment.getCreated());
                    return comment;
                });

        CommentDto actualComment = itemService.createComment(
                requestComment,
                userId,
                itemId);

        assertThat(actualComment.getId(), equalTo(expectedComment.getId()));
        assertThat(actualComment.getText(), equalTo(expectedComment.getText()));
        assertThat(actualComment.getAuthorName(), equalTo(expectedComment.getAuthorName().getName()));
        assertThat(actualComment.getCreated(), equalTo(expectedComment.getCreated()));
    }

    @Test
    void searchItemsTest() throws Exception {
        List<ItemDto> items = Collections.singletonList(ItemMapper.toItemDto(item1));
        when(itemStorage.findAll()).thenReturn(Collections.singletonList(item1));

        List<ItemDto> result = itemService.getItemByText("item");
        assertThat(result, equalTo(items));
    }

    @Test
    void searchItemsTextIsNullTest() throws Exception {
        Exception exception = assertThrows(
                NullPointerException.class,
                () -> itemService.getItemByText(null));
    }

    @Test
    void searchItemsWithoutTextTest() throws Exception {
        List<ItemDto> result = new ArrayList<>();
        assertThat(result, equalTo(itemService.getItemByText("")));
    }
}