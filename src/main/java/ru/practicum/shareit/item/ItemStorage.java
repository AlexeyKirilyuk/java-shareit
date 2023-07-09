package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.User;

import java.util.HashMap;
import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
}
