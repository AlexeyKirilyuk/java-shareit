package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static List<ItemDto> toListItemDto(List<Item> listItem) {
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : listItem) {
            listItemDto.add(toItemDto(item));
        }
        return listItemDto;
    }
}
