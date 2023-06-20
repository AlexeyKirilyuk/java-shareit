package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    String name;            // — краткое название;
    String description;     // — развёрнутое описание;
    boolean available;      // — статус о том, доступна или нет вещь для аренды;
    Integer request;        // — если вещь была создана по запросу другого пользователя, то в этом поле будет храниться Id пользователя.
}