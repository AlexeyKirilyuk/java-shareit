package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemStorage itemStorage;
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        itemStorage.createItem(item);
        return item;
    }

    @PutMapping
    public Item updateUser(@RequestBody Item item) {
        itemStorage.updateItem(item);
        return item;
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable int id) {
        return itemStorage.getItemById(id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteItemById(@PathVariable int id) {
        itemStorage.deleteItemById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
