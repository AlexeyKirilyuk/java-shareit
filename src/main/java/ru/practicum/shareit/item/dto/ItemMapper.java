package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.comments.dto.Comment;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.dto.CommentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();
    }

    public static ItemDto toItemDtoAll(Item item, BookingItemDto last, BookingItemDto next, List<CommentDto> comments) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }

    public static ItemDto toItemDtoAllRegularComments(Item item, BookingItemDto last, BookingItemDto next, List<Comment> comments) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();


        if (comments != null)
            result.setComments(CommentMapper.fromListComment(comments));
        if (last != null)
            result.setLastBooking(last);
        if (next != null)
            result.setNextBooking(next);

        return result;
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
            listItemDto.add(toItemDto(itemOptional.get()));
        }
        return listItemDto;
    }
}
