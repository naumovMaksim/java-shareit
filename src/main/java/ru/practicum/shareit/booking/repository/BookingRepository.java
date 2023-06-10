package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwner(User user, Sort sort);
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime end, Sort sort);
    List<Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime start, Sort sort);
    List<Booking> findAllByItemOwnerAndStatusEquals(User user, Status status, Sort sort);
    List<Booking> findAllByBooker(User user, Sort sort);
    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime end, Sort sort);
    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime start, Sort sort);
    List<Booking> findAllByBookerAndStatusEquals(User user, Status status, Sort sort);
    List<Booking> findAllByItemIdAndStatusEqualsAndEndBeforeOrderByStartAsc(Long itemId, Status status, LocalDateTime now);
    List<Booking> findAllByItemIdAndStatusEqualsAndStartAfterOrderByStartAsc(Long itemId,Status status, LocalDateTime now);
    List<Booking> findAllByItemIdAndEndBeforeOrderByStartAsc(Long itemId, LocalDateTime now);
    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status status, LocalDateTime end);
}
