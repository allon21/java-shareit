package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingDto> bookingItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody BookingDto bookingDto) {
        log.info("Получен POST-запрос к /bookings с userId = {} и телом: {}", userId, bookingDto);
        BookingDto booking = bookingService.createBooking(userId, bookingDto);
        return ResponseEntity.ok().body(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = true) Boolean approved,
                                                     @PathVariable Long bookingId) {
        log.info("Получен PATCH-запрос к /bookings/{} с userId = {} и статусом approved={} ",
                bookingId, userId, approved);
        BookingDto booking = bookingService.approveBooking(userId, bookingId, approved);
        return ResponseEntity.ok().body(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId) {
        log.info("Получен GET-запрос к /bookings/{} с userId = {}", bookingId, userId);
        BookingDto booking = bookingService.findById(userId, bookingId);
        return ResponseEntity.ok().body(booking);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> findByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(defaultValue = "ALL", required = false) StateDto stateDto) {
        log.info("Получен GET-запрос к /bookings с userId = {} и с stateDto = {}", userId, stateDto);
        Collection<BookingDto> bookings = bookingService.findByBookerId(userId, stateDto);
        return ResponseEntity.ok().body(bookings);
    }


    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> findByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @RequestParam(defaultValue = "ALL", required = false) StateDto stateDto) {
        log.info("Получен GET-запрос к /bookings/owner с userId = {} и с stateDto = {}", userId, stateDto);
        Collection<BookingDto> bookings = bookingService.findByOwnerId(userId, stateDto);
        return ResponseEntity.ok().body(bookings);
    }
}
