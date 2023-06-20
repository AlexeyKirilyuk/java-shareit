package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.request.validation.ItemRequestValidation;

import java.util.HashMap;

@Slf4j
@Component("ItemRequestStorageInMemory")
@RequiredArgsConstructor
public class ItemRequestStorageInMemory implements ItemRequestStorage {
    private final ItemRequestValidation itemRequestValidation;
    protected int idItemRequest = 0;
    protected final HashMap<Integer, ItemRequest> itemRequests = new HashMap<>();

    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        if (itemRequestValidation.itemRequestValidation(itemRequest)) {
            idItemRequest++;
            itemRequest.setId(idItemRequest);
            itemRequests.put(idItemRequest, itemRequest);
            log.trace("Добавлен запрос " + itemRequest);
        }
        return itemRequest;
    }

    public int updateItemRequest(ItemRequest itemRequest) {
        if (itemRequestValidation.itemRequestValidation(itemRequest)) {
            if (itemRequests.containsKey(itemRequest.getId())) {
                int id = itemRequest.getId();
                itemRequests.remove(id);
                itemRequests.put(id, itemRequest);
                log.trace("Обновлены данные запроса " + itemRequest);
            } else {
                log.debug("Ошибка - запрос не найден.");
                throw new AlreadyExistException("Ошибка - запрос не найден.");
            }
        }
        return itemRequest.getId();
    }

    public ItemRequest getItemRequestById(int id) {
        if (itemRequests.containsKey(id)) {
            return itemRequests.get(id);
        } else {
            log.debug("Ошибка - не найден такой id");
            throw new AlreadyExistException("Ошибка - не найден такой id");
        }
    }

    public void deleteItemRequestById(int id) {
        itemRequests.remove(id);
    }
}