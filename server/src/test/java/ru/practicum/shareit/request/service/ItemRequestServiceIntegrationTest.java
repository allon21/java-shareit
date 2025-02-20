package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    final ItemRequestServiceImpl itemRequestService;
    final UserService userService;
    Long userId;

    @BeforeEach
    public void setUp() {
        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .name("testUser")
                .build();

        userDto = userService.createUser(userDto);
        userId = userDto.getId();
    }

    @Test
    public void createItemRequestTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequestDto createdRequest = itemRequestService.createItemRequest(userId, itemRequestDto);
        assertNotNull(createdRequest);
        assertEquals("description", createdRequest.getDescription());
        assertNotNull(createdRequest.getId());
    }

    @Test
    public void getItemRequestsByUserTest() {
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder().description("description1").build();
        itemRequestService.createItemRequest(userId, itemRequestDto1);

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder().description("description2").build();
        itemRequestService.createItemRequest(userId, itemRequestDto2);

        Collection<ItemRequestDto> requests = itemRequestService.getItemRequestsByUser(userId);
        assertEquals(2, requests.size(), "Пользователь должен иметь два запроса");
    }

    @Test
    public void getItemRequestByIdTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(userId, itemRequestDto);

        ItemRequestDto foundRequest = itemRequestService.getItemRequestById(createdRequest.getId(), userId);
        assertEquals(createdRequest.getId(), foundRequest.getId());
        assertEquals("description", foundRequest.getDescription());
    }

    @Test
    public void itemRequestNotFoundTest() {
        Long nonExistentRequestId = 999L;

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(nonExistentRequestId, userId),
                "Запрос должен выбросить исключение, если он не найден");
    }
}