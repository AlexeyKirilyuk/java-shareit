package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage itemRequestRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public ItemRequestFullDto create(Long userId, ItemRequestDtoInput requestDtoInput) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new AlreadyExistException("Пользователь с Id = " + userId + " не найден");
        }
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDtoInput(
                requestDtoInput,
                user.get(),
                LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestFullDto> getAll(Long userId) {
        checkUserId(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);
        List<ItemRequestFullDto> result = toItemRequestFullDtoResponse(itemRequests);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestFullDto> getSort(Long userId, Integer from, Integer size) {
        if (from % size != 0) {
            throw new IncorrectParameterException("Некорректный ввод страниц и размеров");
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByRequestor_IdNot(userId, pageable);
        List<ItemRequest> itemRequests = itemRequestPage.getContent();
        List<ItemRequestFullDto> result = toItemRequestFullDtoResponse(itemRequests);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestFullDto getById(Long userId, Long requestId) {
        checkUserId(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new AlreadyExistException("Запрос на предмет с Id = " + requestId + " не найден."));
        List<ItemDto> itemsForRequestDto = itemStorage.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestFullDto result = ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, itemsForRequestDto);
        return result;
    }

    public void checkUserId(Long id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isEmpty()) {
            throw new AlreadyExistException("Пользователь с Id = " + id + " не найден");
        }
    }

    private List<ItemRequestFullDto> toItemRequestFullDtoResponse(List<ItemRequest> itemRequests) {
        return itemRequests.isEmpty() ? Collections.emptyList() : itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemStorage.findAllByRequestId(itemRequest.getId());
                    List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestWithItemsDto(itemRequest, itemsDto);
                })
                .collect(Collectors.toList());
    }
}
