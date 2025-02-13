package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.StateDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private BookingDto bookingDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = start.plusDays(1);

        UserDto booker = UserDto.builder()
                .id(7L)
                .name("booker")
                .email("test@test.com")
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .booker(booker)
                .start(start.withNano(0))
                .end(end.withNano(0))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void bookingItemTest() throws Exception {
        when(bookingService.createBooking(Mockito.anyLong(), Mockito.any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        verify(bookingService, Mockito.times(1))
                .createBooking(Mockito.anyLong(), Mockito.any(BookingDto.class));
    }

    @Test
    public void approveBookingTest() throws Exception {
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 7L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingService, Mockito.times(1))
                .approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    public void findBookingByIdTest() throws Exception {
        when(bookingService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        verify(bookingService, Mockito.times(1))
                .findById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void findAllBookingsByBookerIdTest() throws Exception {
        when(bookingService.findByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 7L)
                        .param("state", StateDto.ALL.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));

        verify(bookingService, Mockito.times(1))
                .findByBookerId(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void findAllBookingsByOwnerIdTest() throws Exception {
        when(bookingService.findByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 7L)
                        .param("state", StateDto.ALL.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));

        verify(bookingService, Mockito.times(1))
                .findByOwnerId(Mockito.anyLong(), Mockito.any());
    }
}