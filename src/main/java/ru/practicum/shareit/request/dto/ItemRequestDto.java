package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemRequestDto {
    private String description;         // — текст запроса, содержащий описание требуемой вещи;
}
