package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemRequestDto {
    String description;         // — текст запроса, содержащий описание требуемой вещи;
}
