package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {
    public Item createItem(Item item);
    public int updateItem(Item item);
    public Item getItemById(int id);
    public void deleteItemById(int id);
}
