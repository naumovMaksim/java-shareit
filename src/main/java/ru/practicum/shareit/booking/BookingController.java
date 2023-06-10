package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@Valid @RequestBody BookingDto bookingDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел POST запрос на добавление новой аренды {} от пользователя с id {}", bookingDto, userId);
        Booking booking = bookingService.create(bookingDto, userId);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestParam Boolean approved) {
        log.info("Пришел /POST запрос на принятие или отклонение аренды от пользователя с id {} к предмету с id {}",
                userId, bookingId);
        Booking booking = bookingService.approve(bookingId, userId, approved);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Пришел /GET запрос на получение данных об аренде с id {} от пользователя {}", bookingId, userId);
        Booking booking = bookingService.getById(bookingId, userId);
        log.info("Ответ отправлен {}", booking);
        return booking;
    }

    @GetMapping("/owner")
    public List<Booking> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для владельца с id {}, и с параметром {}",
                userId, state);
        List<Booking> bookings = bookingService.getAllByOwner(userId, state);
        log.info("Ответ отправлен {}", bookings);
        return bookings;
    }

    @GetMapping
    public List<Booking> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Пришел /GET запрос на получение списка всех бронирований для пользователя с id {}, и с параметром {}",
                userId, state);
        List<Booking> bookings = bookingService.getAllByBooker(userId, state);
        log.info("Ответ отправлен {}", bookings);
        return bookings;
    }
}
