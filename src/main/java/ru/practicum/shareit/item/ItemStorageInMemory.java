package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component("ItemStorageInMemory")
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private final ItemValidation itemValidation;
    private final UserStorage userStorage;
    protected int idItem = 0;
    protected final HashMap<Integer, Item> items = new HashMap<>();

    public Item createItem(Item item, int ownerId) {
        item.setOwner(userStorage.getUserById(ownerId));
        idItem++;
        item.setId(idItem);
        items.put(idItem, item);
        log.trace("Добавлена вещь " + item);
        return item;
    }

    public Item updateItem(int id, Item item, int ownerId) {
        item.setId(id);
        item.setOwner(userStorage.getUserById(ownerId));
        if (items.containsKey(item.getId())) {
            if (item.getName() == null) {
                item.setName(items.get(id).getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(items.get(id).getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(items.get(id).getAvailable());
            }
            items.remove(id);
            items.put(id, item);
            log.trace("Обновлены данные вещи " + item);
        } else {
                log.debug("Ошибка - вещь не найдена.");
                throw new AlreadyExistException("Ошибка - вещь не найдена.");
            }
        return item;
    }

    public Item getItemById(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            log.debug("Ошибка - не найден такой id");
            throw new AlreadyExistException("Ошибка - не найден такой id");
        }
    }

    public List<Item> getItemByOwner(int ownerId) {
        List<Item> list = new ArrayList<>(items.values());
        list.removeIf(item -> item.getOwner().getId() != ownerId);
        return list;
    }

    public List<Item> getItemByText(String text) {
        List<Item> list = new ArrayList<>();
        if (text.isEmpty()) {
            return list;
        }
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable() == Boolean.TRUE) {
                list.add(item);
            }
        }
        return list;
    }

    public void deleteItemById(int id) {
        items.remove(id);
    }

    public HashMap<Integer, Item> getItems() {
        return items;
    }

    public List<Item> getAllItem() {
        return new ArrayList<>(items.values());
    }
}