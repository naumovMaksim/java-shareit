package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findAll(Long userId) {
        List<Item> items = repository.findAllByOwnerIdOrderById(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toItemDto(item);
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream().map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
            itemDto.setLastBooking(bookingRepository.findAllByItemIdAndEndBeforeOrderByStartAsc
                    (itemDto.getId(), LocalDateTime.now()).isEmpty() ? null :
                    toBookingDto(bookingRepository.findAllByItemIdAndEndBeforeOrderByStartAsc
                            (itemDto.getId(), LocalDateTime.now()).get(0)));
            if (itemDto.getLastBooking() != null) {
                itemDto.setNextBooking(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc
                        (itemDto.getId(), LocalDateTime.now()).isEmpty() ? null :
                        toBookingDto(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc
                                (itemDto.getId(), LocalDateTime.now()).get(0)));
            }
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto findById(Long id, Long ownerId) {
        final LocalDateTime now = LocalDateTime.now();
        Item item = repository.findById(id).orElseThrow(
                () -> new DataNotFoundException(String.format("Предмет с id %d не найден", id)));
        ItemDto itemDto = toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(id).stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (ownerId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByEndDesc
                    (id, ownerId, now).isEmpty() ? null :
                    toBookingDto(bookingRepository.findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByEndDesc
                            (id, ownerId, now).get(0)));
            if (itemDto.getLastBooking() != null) {
                itemDto.setNextBooking(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc
                        (itemDto.getId(), LocalDateTime.now()).isEmpty() ? null :
                        toBookingDto(bookingRepository.findAllByItemIdAndStartAfterOrderByStartAsc
                                (id, now).get(0)));
            }
        }
        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        UserDto user = userService.getById(userId);
        Item item = toItem(itemDto);
        item.setOwner(toUser(user));
        repository.save(item);
        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        UserDto user = userService.getById(userId);
        Item item = repository.findById(itemId).orElseThrow(
                () -> new DataNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        if (!item.getOwner().equals(toUser(user))) {
            throw new DataNotFoundException(String.format("Этот предмет не принадлежит пользователю с id %d", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return toItemDto(repository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String textForSearch = text.toLowerCase();
        List<ItemDto> items = new ArrayList<>();
        for (Item item : repository.findAll()) {
            if (item.getName().toLowerCase().contains(textForSearch)
                    || item.getDescription().toLowerCase().contains(textForSearch)) {
                if (item.getAvailable()) {
                    items.add(toItemDto(item));
                }
            }
        }
        return items;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = toUser(userService.getById(userId));
        Item item = repository.findById(itemId).orElseThrow(() ->
                new DataNotFoundException(String.format("Предмет с id %d не найден", itemId)));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, Status.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BookingIsNotAvailableException("Вещь не бралась в аренду или аренда ещё не завершена");
        }
        Comment comment = toComment(commentDto, user, item);
        commentRepository.save(comment);
        return toCommentDto(comment);
    }
}
