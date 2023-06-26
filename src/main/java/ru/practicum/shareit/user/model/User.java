package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class User {
    private int id;                 // — уникальный идентификатор пользователя;
    private String name;            // — имя или логин пользователя;
    private String email;           // — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
}