package ru.practicum.shareit.item.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ItemValidation {
    public boolean itemCreateValidation(Item item, Long ownerId, List<UserDto> listUser) {
            if (item.getName() == null || Objects.equals(item.getName(), "")) {
            String e = "Ошибка валидации - краткое название не может быть пустым";
            log.debug(e);
            throw new ValidationException(e);
        }
        if (item.getDescription() == null) {
            String e = "Ошибка валидации - развёрнутое описание вещи не может быть пустым";
            log.debug(e);
            throw new ValidationException(e);
        }
        if (ownerId == 0) {
            String e = "Ошибка валидации - владелец вещи не может быть пустой";
            log.debug(e);
            throw new ValidationException(e);
        }
        if (item.getAvailable() == null) {
            String e = "Ошибка валидации - статус доступности вещи не может быть пустой";
            log.debug(e);
            throw new ValidationException(e);
        } else {
            for (UserDto userDb : listUser) {
                if (userDb.getId() == ownerId) {
                    return true;
                }
            }
        }
        String e = "Ошибка валидации - владелец вещи не найден.";
        log.debug(e);
        throw new AlreadyExistException(e);
    }

    public boolean itemUpdateValidation(Long id, Long ownerId, List<Item> items) {
        List<Item> itemss = new ArrayList<>(items);
        if (ownerId == 0) {
            String e = "Ошибка валидации - владелец вещи не может быть пустой";
            log.debug(e);
            throw new ValidationException(e);
        }
        for (Item savedItem : itemss) {
            if (Objects.equals(savedItem.getId(), id)) {
                if (!Objects.equals(savedItem.getOwner().getId(), ownerId)) {
                    String e = "Ошибка - у вещи другой владелец.";
                    log.debug(e);
                    throw new AlreadyExistException(e);
                }
            }
        }
        return true;
    }
}
