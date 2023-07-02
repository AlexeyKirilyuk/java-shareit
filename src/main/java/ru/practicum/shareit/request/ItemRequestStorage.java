package ru.practicum.shareit.request;

public interface ItemRequestStorage {
    public ItemRequest createItemRequest(ItemRequest itemRequest);

    public int updateItemRequest(ItemRequest itemRequest);

    public ItemRequest getItemRequestById(int id);

    public void deleteItemRequestById(int id);

}
