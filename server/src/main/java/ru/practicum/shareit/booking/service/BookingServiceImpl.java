package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.StateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAccessException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto createBooking(Long bookerId, BookingDto bookingDto) {
        User user = UserMapper.INSTANCE.toUser(userService.getUserById(bookerId));
        Long itemId = bookingDto.getItemId();
        if (itemId == null) {
            throw new NotFoundException("Объект не найден, id = " + bookerId);
        }
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + bookerId));
        if (item.getOwner().getId() == bookerId) {
            throw new UserAccessException("Владелец не может забронировать свой объект.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Нет доступа");
        }
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        return BookingMapper.INSTANCE.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + bookingId));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new UserAccessException("Нет доступа");
        }

        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.INSTANCE.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + bookingId));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.INSTANCE.toBookingDto(booking);
        } else {
            throw new UserAccessException("Ошибка доступа.");
        }
    }


    @Override
    public Collection<BookingDto> findByBookerId(Long bookerId, StateDto stateDto) {
        List<Booking> bookings = Collections.emptyList();

        switch (stateDto) {
            case ALL -> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
            case WAITING -> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.REJECTED);
            case CURRENT ->
                    bookings = bookingRepository.findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
                            bookerId, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookings = bookingRepository.findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                    bookerId, BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookings = bookingRepository.findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
                    bookerId, BookingStatus.APPROVED, LocalDateTime.now());
        }
        return bookings.stream()
                .map(BookingMapper.INSTANCE::toBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> findByOwnerId(Long bookerId, StateDto stateDto) {
        userService.getUserById(bookerId);
        List<Booking> bookings = Collections.emptyList();

        switch (stateDto) {
            case ALL -> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(bookerId);
            case WAITING -> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.REJECTED);
            case CURRENT ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
                            bookerId, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookings = bookingRepository.findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
                    bookerId, BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookings = bookingRepository.findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(
                    bookerId, BookingStatus.APPROVED, LocalDateTime.now());
        }
        return bookings.stream()
                .map(BookingMapper.INSTANCE::toBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> findByItemAndBooker(Item item, User booker) {
        List<Booking> result = bookingRepository.findByBookerAndItemAndStatusAndEndBefore(booker, item,
                BookingStatus.APPROVED, LocalDateTime.now());
        return result.stream()
                .map(BookingMapper.INSTANCE::toBookingDto)
                .toList();
    }

    @Override
    public Optional<Booking> findLastBooking(Item item) {
        List<Booking> lastBookingList = bookingRepository.findFirstByItemAndStatusEndBefore(item,
                BookingStatus.APPROVED,
                LocalDateTime.now().minusMinutes(1));
        return lastBookingList.isEmpty() ? Optional.empty() : Optional.of(lastBookingList.getFirst());
    }

    @Override
    public Optional<Booking> findNextBooking(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(item, BookingStatus.APPROVED,
                LocalDateTime.now());
    }
}

