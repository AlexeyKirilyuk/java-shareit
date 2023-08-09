package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ItemValidation {
    public boolean itemCreateValidation(Item item, int ownerId, List<Item> items, List<UserDto> listUser) {
        if (item.getName() == null || Objects.equals(item.getName(), "")) {
            log.debug("Ошибка валидации - краткое название не может быть пустым");
            throw new ValidationException("Ошибка валидации - краткое название не может быть пустым");
        }
        if (item.getDescription() == null) {
            log.debug("Ошибка валидации - развёрнутое описание вещи не может быть пустым");
            throw new ValidationException("Ошибка валидации - развёрнутое описание вещи не может быть пустым");
        }
        if (ownerId == 0) {
            log.debug("Ошибка валидации - владелец вещи не может быть пустой");
            throw new ValidationException("Ошибка валидации - владелец вещи не может быть пустой");
        }
        if (item.getAvailable() == null) {
            log.debug("Ошибка валидации - статус доступности вещи не может быть пустой");
            throw new ValidationException("Ошибка валидации - статус доступности вещи не может быть пустой");
        } else {
            for (UserDto userDb : listUser) {
                log.debug("userDb.getId() = " + userDb.getId() + " ownerId = " + ownerId);
                if (userDb.getId() == ownerId) {
                    return true;
                }
            }
        }
        log.debug("Ошибка валидации - владелец вещи не найден.");
        throw new AlreadyExistException("Ошибка валидации - владелец вещи не найден.");


    }

    public boolean itemUpdateValidation(Long id, Item item, Long ownerId, List<Item> items) {
        List<Item> itemss = new ArrayList<>(items);
        if (ownerId == 0) {
            log.debug("Ошибка валидации - владелец вещи не может быть пустой");
            throw new ValidationException("Ошибка валидации - владелец вещи не может быть пустой");
        }
        for (Item savedItem : itemss) {
            if (Objects.equals(savedItem.getId(), id)) {
                if (!Objects.equals(savedItem.getOwner().getId(), ownerId)) {
                    log.debug("Ошибка - у вещи другой владелец.");
                    throw new AlreadyExistException("Ошибка - у вещи другой владелец.");
                }
            }
        }
        return true;
    }
}
