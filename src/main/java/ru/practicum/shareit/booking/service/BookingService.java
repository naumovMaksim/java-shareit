package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, Long userId);
    Booking approve(Long bookingId, Long ownerId, Boolean approve);
    Booking getById(Long bookingId, Long userId);
    List<Booking> getAllByOwner(Long userId, String state);
    List<Booking> getAllByBooker(Long userId, String state);
}
