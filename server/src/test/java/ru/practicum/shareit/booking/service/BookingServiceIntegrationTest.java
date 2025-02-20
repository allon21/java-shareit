package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    final BookingService bookingService;
    final UserService userService;
    final ItemService itemService;

    Long userId;
    Long ownerId;
    Long itemId;

    @BeforeEach
    public void setUp() {
        UserDto ownerDto = UserDto.builder()
                .email("owner@test.com")
                .name("ownerName")
                .build();
        UserDto bookerDto = UserDto.builder()
                .email("booker@test.com")
                .name("bookerName")
                .build();

        ownerDto = userService.createUser(ownerDto);
        bookerDto = userService.createUser(bookerDto);
        ownerId = ownerDto.getId();
        userId = bookerDto.getId();

        ItemDto itemDto = ItemDto.builder()
                .name("TestItem")
                .description("TestItemDescription")
                .available(true)
                .build();

        itemDto = itemService.createItem(ownerId, itemDto);
        itemId = itemDto.getId();
    }

    @Test
    public void createBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto createdBooking = bookingService.createBooking(userId, bookingDto);
        assertNotNull(createdBooking.getId(), "Iв бронирования не должен быть пустым");
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus(), "Booking status должен быть WAITING");
    }

    @Test
    public void approveBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto createdBooking = bookingService.createBooking(userId, bookingDto);
        BookingDto approvedBooking = bookingService.approveBooking(ownerId, createdBooking.getId(), true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus(), "Booking status должен быть APPROVED");
    }

    @Test
    public void findBookingsByBookerIdTest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.createBooking(userId, bookingDto);
        Collection<BookingDto> bookings = bookingService.findByBookerId(userId, StateDto.ALL);

        assertFalse(bookings.isEmpty(), "Необходимо вернуть хотя бы одно бронирование");
        assertEquals(1, bookings.size(), "Необходимо вернуть одно бронирование");
    }

    @Test
    public void findBookingsByOwnerIdTest() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingService.createBooking(userId, bookingDto);
        Collection<BookingDto> bookings = bookingService.findByOwnerId(ownerId, StateDto.ALL);

        assertFalse(bookings.isEmpty(), "Необходимо вернуть хотя бы одно бронирование");
        assertEquals(1, bookings.size(), "Необходимо вернуть одно бронирование");
    }
}