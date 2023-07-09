package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {
    public ItemDto createItem(ItemDto itemDto, int ownerId);
    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId);
    public ItemDto getItemById(int id);
    public void deleteItemById(int id);
    public List<ItemDto> getAllItem();
    public List<ItemDto> getItemByOwner(int ownerId);
    public List<ItemDto> getItemByText(String text);
}
