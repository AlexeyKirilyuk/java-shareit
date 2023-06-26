package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody Item item, @PathVariable int id, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.updateItem(id, item, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getItemByOwner(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getItemByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteItemById(@PathVariable int id) {
        itemService.deleteItemById(id);
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
