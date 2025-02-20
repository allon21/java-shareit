package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface BookingService {
    BookingDto findById(Long userId, Long bookingId);

    BookingDto createBooking(Long bookerId, BookingDto bookingDto);

    BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approve);

    Collection<BookingDto> findByBookerId(Long bookerId, StateDto stateDto);

    Collection<BookingDto> findByOwnerId(Long bookerId, StateDto stateDto);

    Collection<BookingDto> findByItemAndBooker(Item item, User booker);

    Optional<Booking> findLastBooking(Item item);

    Optional<Booking> findNextBooking(Item item);
}
