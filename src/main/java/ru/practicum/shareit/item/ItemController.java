package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @PostMapping
    public Item createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        itemStorage.createItem(item, ownerId);
        return item;
    }

    @PatchMapping("/{id}")
    public Item updateItem(@RequestBody Item item, @PathVariable int id, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        item.setId(id);
        itemStorage.updateItem(item, ownerId);
        return item;
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable int id) {
        return itemStorage.getItemById(id);
    }

    @GetMapping
    public List<Item> getItemByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemStorage.getItemByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> getItemByText(@RequestParam String text) {
        return itemStorage.getItemByText(text);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteItemById(@PathVariable int id) {
        itemStorage.deleteItemById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                               //Status code is 400
    public Map<String, String> validationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                                 //Status code is 404
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                    //Status code is 409
    public Map<String, String> conflictException(final ConflictException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
