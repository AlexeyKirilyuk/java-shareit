package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    String name;            // — имя или логин пользователя;
    String email;           // — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
}
