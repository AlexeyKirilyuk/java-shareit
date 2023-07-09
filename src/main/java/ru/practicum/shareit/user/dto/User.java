package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                 // — уникальный идентификатор пользователя;
    @Column(name = "name")
    private String name;            // — имя или логин пользователя;
    @Column(name = "email")
    private String email;           // — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
}