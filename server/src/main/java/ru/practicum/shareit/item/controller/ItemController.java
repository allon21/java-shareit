package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
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
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос к /items с userId = {} и телом: {}", userId, itemDto);
        ItemDto newItem = itemService.createItem(userId, itemDto);
        return ResponseEntity.ok().body(newItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        log.info("Получен PATCH-запрос к /items/{} с userId = {} и телом: {}", itemId, userId, itemDto);
        ItemDto updatedItem = itemService.updateItem(userId, itemDto, itemId);
        return ResponseEntity.ok().body(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получен DELETE-запрос к /items/{} с userId = {}", itemId, ownerId);
        ItemDto deletedItem = itemService.deleteItem(itemId, ownerId);
        return ResponseEntity.ok().body(deletedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен GET-запрос к /items/{} с userId = {}", itemId, userId);
        ItemDto itemDto = itemService.getItemById(itemId);
        return ResponseEntity.ok().body(itemDto);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getUserAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос к /items с userId = {}", userId);
        Collection<ItemDto> items = itemService.getUserAllItems(userId);
        return ResponseEntity.ok().body(items);
    }


    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam String text) {
        log.info("Получен GET-запрос к /items/search с userId = {} и текстом: {}", userId, text);
        Collection<ItemDto> itemsByText = itemService.searchItems(text);
        return ResponseEntity.ok().body(itemsByText);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @RequestBody CommentDto commentDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Пришел POST-запрос к /items/{}/comment с телом: {} и с userId ={}",
                itemId, commentDto, userId);
        CommentDto comment = itemService.addComment(itemId, commentDto, userId);
        return ResponseEntity.ok().body(comment);
    }
}

