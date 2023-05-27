package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAll();

    Item findById(Long id);

    Item create(Item item);

    Item update(Item item);
}
