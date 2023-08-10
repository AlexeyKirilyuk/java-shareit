package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

@Valid
@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestFullDto createRequest(@Valid @RequestBody ItemRequestDtoInput itemRequestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestFullDto> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from, @Positive @RequestParam(defaultValue = "10") Integer size) {
     return (itemRequestService.getSort(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                                 //Status code is 404
    public Map<String, String> alreadyExistException(final AlreadyExistException e) {
        return Map.of("error", "Ошибка",
                "errorMessage", e.getMessage());
    }
}
