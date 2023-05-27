package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public List<ItemDto> findAll(Long userId) {
        UserDto user = userService.getById(userId);
        List<ItemDto> items = new ArrayList<>();
        for (Item item : repository.findAll()) {
            if (item.getOwner().equals(toUser(user))) {
                items.add(toItemDto(item));
            }
        }
        return items;
    }

    @Override
    public ItemDto findById(Long id) {
        Item item = repository.findById(id);
        if (item == null) {
            throw new DataNotFoundException(String.format("Предмет с id %d не найден", id));
        }
        return toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        UserDto user = userService.getById(userId);
        Item item = toItem(itemDto, user);
        return toItemDto(repository.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        UserDto user = userService.getById(userId);
        Item item = repository.findById(itemId);
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
        return toItemDto(item);
    }

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
}
