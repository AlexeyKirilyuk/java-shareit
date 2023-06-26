package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final ItemValidation itemValidation;

    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        if (itemValidation.itemCreateValidation(item, ownerId, itemStorage.getItems())) {
            return ItemMapper.toItemDto(itemStorage.createItem(item, ownerId));
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        if (itemValidation.itemUpdateValidation(id, item, ownerId, itemStorage.getItems())) {
            return ItemMapper.toItemDto(itemStorage.updateItem(id, item, ownerId));
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    public void deleteItemById(int id) {
        itemStorage.deleteItemById(id);
    }

    public List<ItemDto> getAllItem() {
        return ItemMapper.toListItemDto(itemStorage.getAllItem());
    }

    public List<ItemDto> getItemByOwner(int ownerId) {
        return ItemMapper.toListItemDto(itemStorage.getItemByOwner(ownerId));
    }

    public List<ItemDto> getItemByText(String text) {
        return ItemMapper.toListItemDto(itemStorage.getItemByText(text));
    }
}
