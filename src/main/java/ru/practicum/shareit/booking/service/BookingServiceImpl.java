package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingIsNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final static Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public Booking create(BookingDto bookingDto, Long userId) {
        User user = toUser(userService.getById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                ()-> new DataNotFoundException ("Предмет не найден"));
        if (userId.equals(item.getOwner().getId())) {
            throw new DataNotFoundException("Невозможно создать бронирование своей же ввещи");
        }
        if (!item.getAvailable()) {
            throw new BookingIsNotAvailableException("Данная вещь недоступна");
        }
        Booking booking = toBooking(bookingDto, item, user);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BookingIsNotAvailableException("Дата окончания бронирования не может быть больше даты начала или равна 0");
        }
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long bookingId, Long ownerId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new DataNotFoundException("Бронирование не найдено"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new DataNotFoundException(String.format("Бронирование у пользователя с id %d не найдено", ownerId));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BookingIsNotAvailableException("Бронирование уже подтверждено или отклонено");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Booking getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new DataNotFoundException("Бронирование не найдено"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new DataNotFoundException("Вы не владелец или автор бронирования");
        }

        return booking;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getAllByOwner(Long userId, String state) {
        User user = toUser(userService.getById(userId));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByItemOwner(user, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort));
                break;
            default:
                throw new BookingIsNotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Booking> getAllByBooker(Long userId, String state) {
        User user = toUser(userService.getById(userId));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBooker(user, sort));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, sort));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort));
                break;
            default:
                throw new BookingIsNotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}
