package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Optional<Item>  item) {
        return new ItemDto(
                item.get().getId(),
                item.get().getName(),
                item.get().getDescription(),
                item.get().getAvailable(),
                item.get().getOwner() != null ? item.get().getOwner(): null,
                item.get().getRequest() != null ? item.get().getRequest(): null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }

    public static List<ItemDto> toListItemDto(List<Item> listItem) {
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : listItem) {
            Optional<Item> itemOptional = Optional.of(item);
            listItemDto.add(toItemDto(itemOptional));
        }
        return listItemDto;
    }
}
