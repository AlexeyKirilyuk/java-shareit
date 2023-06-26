package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;                 // — уникальный идентификатор пользователя;
    private String name;            // — имя или логин пользователя;
    private String email;           // — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
}