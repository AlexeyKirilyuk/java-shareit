package ru.practicum.shareit.request;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;

public interface ItemRequestStorage {
    public ItemRequest createItemRequest(ItemRequest itemRequest);
    public int updateItemRequest(ItemRequest itemRequest);
    public ItemRequest getItemRequestById(int id);
    public void deleteItemRequestById(int id);

}
