package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest fromItemRequestDtoInput(ItemRequestDtoInput request, User user, LocalDateTime time) {
        return ItemRequest.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(user)
                .created(time)
                .build();
    }

    public static ItemRequestFullDto toItemRequestWithItemsDto(ItemRequest itemRequest,
                                                               List<ItemDto> items) {
        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(new User(itemRequest.getRequestor().getId(), itemRequest.getRequestor().getName(), itemRequest.getRequestor().getEmail()))
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
