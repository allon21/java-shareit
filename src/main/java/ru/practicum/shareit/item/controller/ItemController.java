package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос к /items с userId = {} и телом: {}", userId, itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("Получен PATCH-запрос к /items/{} с userId = {} и телом: {}", itemId, userId, itemDto);
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен GET-запрос к /items/{} с userId = {}", itemId, userId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getUserAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос к /items с userId = {}", userId);
        return itemService.getUserAllItems(userId);
    }


    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.info("Получен GET-запрос к /items/search с userId = {} и текстом: {}", userId, text);
        return itemService.searchItems(text);
    }
}
