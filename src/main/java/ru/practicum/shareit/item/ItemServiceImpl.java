package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentStorage;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemValidation itemValidation;
    private final UserStorage userStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final UserServiceImpl userService;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {

        Item item = ItemMapper.toItem(itemDto);

        userService.getAllUser();
        if (itemValidation.itemCreateValidation(item, ownerId, userService.getAllUser())) {
            User user = checkUser((long) ownerId);
            item.setOwner(user);
            Long itemRequestId = itemDto.getRequestId();
            if (itemRequestId != null) {
                item.setRequest(itemRequestStorage.findById(itemRequestId)
                    .orElseThrow(() -> new AlreadyExistException("Запрос с Id = " + itemRequestId + " не найден")));
            }
            Optional<Item> itemOptional = Optional.of(itemStorage.save(item));
            return ItemMapper.toItemDto(itemOptional.get());
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId) {

        Item item = ItemMapper.toItem(itemDto);
        if (itemValidation.itemUpdateValidation(id, ownerId, itemStorage.findAll())) {
            Item itemDb = checkItem(id);

            item.setOwner(checkUser(ownerId));

            if (item.getName() != null) {
                itemDb.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemDb.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemDb.setAvailable(item.getAvailable());
            }
            Optional<Item> itemOptional = Optional.of(itemStorage.save(itemDb));
            return ItemMapper.toItemDto(itemOptional.get());
        }
        log.debug("Ошибка валидации");
        throw new ValidationException("Ошибка валидации");
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = checkItem(itemId);
        return addBookingAndComment(item, userId);
    }

    @Override
    public void deleteItemById(Long id) {
        itemStorage.deleteById(id);
    }

    @Override
    public List<ItemDto> getAllItem() {
        return ItemMapper.toListItemDto(itemStorage.findAll());
    }

    @Override
    public List<ItemDto> getItemByOwner(Long ownerId) {
        List<Item> items = itemStorage.findAllByOwnerId(ownerId);
        List<ItemDto> result = new ArrayList<>();
        List<Long> itemsId = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Item, List<Comment>> comments = commentStorage.findAllByItemIdIn(itemsId)
                .stream().collect(Collectors.groupingBy(Comment::getItem));

        Map<Item, Booking> lastBookings = bookingStorage.getLastBookings(ownerId, LocalDateTime.now())
                .stream().collect(Collectors.toMap(Booking::getItem, Function.identity()));

        Map<Item, Booking> nextBookings = bookingStorage.getNextBookings(ownerId, LocalDateTime.now())
                .stream().collect(Collectors.toMap(Booking::getItem, Function.identity()));

        for (Item item : items) {
            ItemDto itemFullDto = ItemMapper.toItemDtoAllRegularComments(
                    item,
                    BookingMapper.toBookingItemDto(lastBookings.get(item)),
                    BookingMapper.toBookingItemDto(nextBookings.get(item)),
                    comments.get(item)
            );
            result.add(itemFullDto);
        }
        log.trace("Завершение вызова метода getAllUserItems");
        return result;
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

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        log.debug("Вызов метода createComment с itemId = {}, userId = {}", itemId, userId);
        User user = checkUser(userId);
        Item item = checkItem(itemId);
        if (!bookingStorage.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()))
            throw new ValidationException("Невозможно оставить комментарий");

        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ValidationException("Отсутствует входной текст");
        Comment comment = CommentMapper.fromCommentDto(commentDto, item, user);
        comment = commentStorage.save(comment);
        log.trace("Завершение вызова метода createComment");
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto addBookingAndComment(Item item, Long userId) {
        log.trace("Вызов метода addBookingAndComment с itemId = {}, userId = {}", item.getId(), userId);
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (userId.equals(item.getOwner().getId())) {
            lastBooking = bookingStorage.getLastBooking(item.getId());
            if (lastBooking == null || lastBooking.getBooker().getId().equals(item.getOwner().getId())) {
                lastBooking = null;
            } else {
                nextBooking = bookingStorage.getNextBooking(item.getId(), lastBooking.getEnd());
            }
        }
        List<CommentDto> comments = CommentMapper.fromListComment(commentStorage.findAllByItemId(item.getId()));

        BookingItemDto last = (lastBooking == null ? null : BookingMapper.toBookingItemDto(lastBooking));
        BookingItemDto next = (nextBooking == null ? null : BookingMapper.toBookingItemDto(nextBooking));

        return ItemMapper.toItemDtoAll(item, last, next, comments);
    }

    private User checkUser(Long userId) {
        log.trace("Вызов метода checkUser с userId = {}", userId);
        Optional<User> user = userStorage.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new AlreadyExistException("Пользователь с id = " + userId + " не найден");
        }
    }

    private Item checkItem(Long itemId) {
        log.trace("Вызов метода checkItem с itemId = {}", itemId);
        Optional<Item> item = itemStorage.findById(itemId);
        if (item.isPresent()) {
            return item.get();
        } else {
            throw new AlreadyExistException("Предмет с id = " + itemId + " не найден");
        }
    }
}

