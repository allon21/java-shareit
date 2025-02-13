package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    final ItemService itemService;
    final UserService userService;
    Long userId;

    @BeforeEach
    public void setUp() {
        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .name("name")
                .build();

        userDto = userService.createUser(userDto);
        userId = userDto.getId();

        ItemDto itemNotAvailableTextInName = ItemDto.builder()
                .name("ItemTextName")
                .description("ItemDescription")
                .available(false)
                .build();

        itemService.createItem(userId, itemNotAvailableTextInName);

        ItemDto itemNotAvailableTextInDesc = ItemDto.builder()
                .name("ItemName")
                .description("ItemTextDescription")
                .available(false)
                .build();

        itemService.createItem(userId, itemNotAvailableTextInDesc);

        ItemDto itemAvailableTextInName = ItemDto.builder()
                .name("ItemNameText")
                .description("ItemDescription")
                .available(true)
                .build();

        itemService.createItem(userId, itemAvailableTextInName);

        ItemDto itemAvailableTextInDesc = ItemDto.builder()
                .name("ItemName")
                .description("TextItemDescription")
                .available(true)
                .build();

        itemService.createItem(userId, itemAvailableTextInDesc);
    }

    @Test
    public void findByTextTests() {
        Collection<ItemDto> itemDtos = itemService.searchItems("text");
        assertEquals(2, itemDtos.size());
    }

    @Test
    public void getAllItemsTest() {
        Collection<ItemDto> items = itemService.getUserAllItems(userId);
        assertEquals(4, items.size(), "Всего у пользователя должно быть четыре элемента");
    }
}