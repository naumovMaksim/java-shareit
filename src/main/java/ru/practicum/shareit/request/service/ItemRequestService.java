package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestResponseDto> getAllByUser(Long userId);
    List<ItemRequestResponseDto> getAll(int from, int size, Long userId);
    ItemRequestResponseDto getById(Long requestId, Long userId);
}
