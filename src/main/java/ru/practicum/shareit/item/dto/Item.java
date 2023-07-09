package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "ITEM")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                 // — уникальный идентификатор вещи;
    @Column(name = "name")
    private String name;            // — краткое название;
    @Column(name = "description")
    private String description;     // — развёрнутое описание;
    @Column(name = "is_available")
    private Boolean available;      // — статус о том, доступна или нет вещь для аренды;
    @Column(name = "owner_id")
    private Integer owner;             // — владелец вещи;
    @Column(name = "request_id")
    private Integer request;    // — если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.




}
