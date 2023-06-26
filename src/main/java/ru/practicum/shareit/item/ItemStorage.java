package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;

public interface ItemStorage {
    public Item createItem(Item item, int ownerId);

    public Item updateItem(int id, Item item, int ownerId);

    public Item getItemById(int id);

    public List<Item> getItemByOwner(int ownerId);

    public List<Item> getItemByText(String text);

    public void deleteItemById(int id);

    public List<Item> getAllItem();

    public HashMap<Integer, Item> getItems();
}
