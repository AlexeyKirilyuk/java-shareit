package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private int id;                 // — уникальный идентификатор бронирования;
    private LocalDateTime start;    // — дата и время начала бронирования;
    private LocalDateTime end;      // — дата и время конца бронирования;
    private Item item;              // — вещь, которую пользователь бронирует;
    private User booker;            // — пользователь, который осуществляет бронирование;
    private Status status;          // — статус бронирования. Может принимать одно из следующих значений:
    // WAITING — новое бронирование, ожидает одобрения,
    // APPROVED — Дополнительные советы ментора 2 бронирование подтверждено владельцем,
    // REJECTED — бронирование отклонено владельцем,
    // CANCELED — бронирование отменено создателем
}
