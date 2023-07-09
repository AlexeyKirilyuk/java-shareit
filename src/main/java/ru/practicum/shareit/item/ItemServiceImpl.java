package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService  {

    private final ItemStorage itemStorage;
    private final ItemValidation itemValidation;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        if (itemValidation.itemCreateValidation(item, ownerId, itemStorage.findAll())) {
            Optional<Item> itemOptional = Optional.of(itemStorage.save(item));
            return ItemMapper.toItemDto(itemOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    @Transactional
    public ItemDto updateItem(int id, ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        if (itemValidation.itemUpdateValidation(id, item, ownerId, itemStorage.findAll())) {
            Optional<Item> itemOptional = Optional.of(itemStorage.save(item));
            return ItemMapper.toItemDto(itemOptional);
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(itemStorage.findById((long) id));
    }
    @Override
    public void deleteItemById(int id) {
        itemStorage.deleteById((long) id);
    }
    @Override
    public List<ItemDto> getAllItem() {
        return ItemMapper.toListItemDto(itemStorage.findAll());
    }
    @Override
    public List<ItemDto> getItemByOwner(int ownerId) {
        List<Item> list = new ArrayList<>(itemStorage.findAll());
        list.removeIf(item -> item.getOwner() != ownerId);
        return ItemMapper.toListItemDto(list);
    }
    @Override
    public List<ItemDto> getItemByText(String text) {
        List<Item> list = new ArrayList<>();
        if (text.isEmpty()) {
            return ItemMapper.toListItemDto(list);
        }
        for (Item item : itemStorage.findAll()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase())
                    && item.getAvailable() == Boolean.TRUE) {
                list.add(item);
            }
        }
        return ItemMapper.toListItemDto(list);
    }
}

