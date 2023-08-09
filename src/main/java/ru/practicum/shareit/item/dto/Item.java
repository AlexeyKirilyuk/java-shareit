package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@Table(name = "ITEMS")
@AllArgsConstructor
@NoArgsConstructor
public class Item {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;                 // — уникальный идентификатор вещи;

    @Column(name = "name")
    private String name;            // — краткое название;

    @Column(name = "description")
    private String description;     // — развёрнутое описание;

    @Column(name = "is_available")
    private Boolean available;      // — статус о том, доступна или нет вещь для аренды;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;             // — владелец вещи;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;   // — если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос.

    public Item(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
