package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    private ItemDto item1;
    private ItemDto item2;
    private ItemDto itemFull;
    private CommentDto comment;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        comment = CommentDto.builder()
                .id(1L)
                .text("Текст комента")
                .authorName("Имя автора")
                .created(LocalDateTime.now())
                .build();
        item1 = ItemDto.builder()
                .id(1L)
                .name("Item_1")
                .description("Описание Item 1")
                .available(true)
                .build();
        item2 = ItemDto.builder()
                .id(2L)
                .name("Item_2")
                .description("Описание Item 2")
                .available(true)
                .build();
        itemFull = ItemDto.builder()
                .id(2L)
                .name("ItemFull_2")
                .description("Описание ItemFull_2")
                .available(false)
                .nextBooking(null)
                .lastBooking(null)
                .comments(Collections.singletonList(comment))
                .build();
    }

    @Test
    void getUserItems() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemService.getItemByOwner(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(item2.getAvailable())));
    }

    @Test
    void getUserItemsValidationException() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemService.getItemByOwner(anyLong())).thenThrow(new ValidationException("Ошибка валидации"));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserItemsAlreadyExistException() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemService.getItemByOwner(anyLong())).thenThrow(new AlreadyExistException("Ошибка"));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserItemsConflictException() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemService.getItemByOwner(anyLong())).thenThrow(new ConflictException("Ошибка"));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getItemById() throws Exception {

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemFull);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 2).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemFull.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFull.getName())))
                .andExpect(jsonPath("$.description", is(itemFull.getDescription())))
                .andExpect(jsonPath("$.available", is(itemFull.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemFull.getRequestId())))
                .andExpect(jsonPath("$.comments", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.lastBooking", is(itemFull.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemFull.getNextBooking())));
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> items = Collections.singletonList(item1);

        when(itemService.getItemByText(anyString())).thenReturn(items);

        mockMvc.perform(
                        get("/items/search")
                                .param("text", "item")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())));
    }

    @Test
    void addItem() throws Exception {
        when(itemService.createItem(Mockito.any(ItemDto.class), anyLong())).thenReturn(item1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), Mockito.any(ItemDto.class), anyLong())).thenReturn(item1);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.createComment(Mockito.any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                //TODO поискать решение проблемы
                //.andExpect(jsonPath("$.created", is(comment1.getCreated().toString())));
                .andExpect(jsonPath("$.created", Matchers.notNullValue()));

    }
}
