package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component("ItemStorageInMemory")
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private final ItemValidation itemValidation;
    protected int idItem = 0;
    protected final HashMap<Integer, Item> items = new HashMap<>();

    public Item createItem(Item item) {
        if (itemValidation.itemValidation(item)) {
            idItem++;
            item.setId(idItem);
            items.put(idItem, item);
            log.trace("Добавлена вещь " + item);
        }
        return item;
    }

    public int updateItem(Item item) {
        if (itemValidation.itemValidation(item)) {
            if (items.containsKey(item.getId())) {
                int id = item.getId();
                items.remove(id);
                items.put(id, item);
                log.trace("Обновлены данные вещи " + item);
            } else {
                log.debug("Ошибка - вещь не найдена.");
                throw new AlreadyExistException("Ошибка - вещь не найдена.");
            }
        }
        return item.getId();
    }

    public Item getItemById(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            log.debug("Ошибка - не найден такой id");
            throw new AlreadyExistException("Ошибка - не найден такой id");
        }
    }

    public void deleteItemById(int id) {
        items.remove(id);
    }
}