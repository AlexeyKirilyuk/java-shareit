package ru.practicum.shareit.item;

import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    ItemDto createItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId);

    ItemDto getItemById(Long userId, Long itemId);

    void deleteItemById(Long id);

    List<ItemDto> getAllItem();

    List<ItemDto> getItemByOwner(Long ownerId);

    List<ItemDto> getItemByText(String text);

    CommentDto createComment(CommentDto comment, Long userId, Long itemId);
}
