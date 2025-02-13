package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    ItemRequestDto itemRequestDto;
    LocalDateTime created;
    DateTimeFormatter formatter;
    ItemDto itemDto;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        itemDto = ItemDto.builder()
                .id(5L)
                .name("ItemName")
                .description("ItemDescription")
                .available(false)
                .requestId(1L)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(created.withNano(0))
                .items(List.of(itemDto))
                .userId(7L)
                .build();
    }

    @Test
    public void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.items.[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.userId", is(itemRequestDto.getUserId()), Long.class));

        verify(itemRequestService, Mockito.times(1))
                .createItemRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class));
    }

    @Test
    public void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is("description")))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.items.[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.userId", is(itemRequestDto.getUserId()), Long.class));

        verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void getAllItemRequestsTest() throws Exception {
        when(itemRequestService.getAllItemRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 7L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].items.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].items.[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].userId", is(itemRequestDto.getUserId()), Long.class));

        verify(itemRequestService, Mockito.times(1))
                .getAllItemRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void getItemRequestsByUserTest() throws Exception {
        when(itemRequestService.getItemRequestsByUser(Mockito.anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is("description")))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated().format(formatter))))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].items.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].items.[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].userId", is(itemRequestDto.getUserId()), Long.class));

        verify(itemRequestService, Mockito.times(1))
                .getItemRequestsByUser(Mockito.anyLong());
    }

}