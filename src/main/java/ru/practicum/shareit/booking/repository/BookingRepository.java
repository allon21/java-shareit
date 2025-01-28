package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByItemAndStatusAndStartAfterOrderByStart(
            Item item, BookingStatus status, LocalDateTime start);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, BookingStatus status, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
            Long bookerId, BookingStatus status, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            Long bookerId, BookingStatus status, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, BookingStatus status, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(
            Long ownerId, BookingStatus status, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
            Long ownerId, BookingStatus status, LocalDateTime end);

    List<Booking> findByBookerAndItemAndStatusAndEndBefore(
            User booker, Item item, BookingStatus status, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE (b.item = :item) " +
            "AND (b.status = :status) " +
            "AND (b.end < :topMoment)" +
            "ORDER BY b.end DESC")
    List<Booking> findFirstByItemAndStatusEndBefore(@Param("item") Item item,
                                                    @Param("status") BookingStatus status,
                                                    @Param("topMoment") LocalDateTime topMoment,
                                                    Pageable pageable);
}