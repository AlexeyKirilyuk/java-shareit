package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private int id;                 // — уникальный идентификатор вещи;
    private String name;            // — краткое название;
    private String description;     // — развёрнутое описание;
    private boolean available;      // — статус о том, доступна или нет вещь для аренды;
    private Integer request;        // — если вещь была создана по запросу другого пользователя, то в этом поле будет храниться Id пользователя.
}