package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create item request: userId={}, itemRequestDto={}", userId, itemRequestDto);
        ItemRequestDto createdRequest = itemRequestService.createItemRequest(userId, itemRequestDto);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Get item request by ID: userId={}, requestId={}", userId, requestId);
        ItemRequestDto itemRequest = itemRequestService.getItemRequestById(requestId, userId);
        return ResponseEntity.ok(itemRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get all item requests: userId={}, from={}, size={}", userId, from, size);
        Collection<ItemRequestDto> requests = itemRequestService.getAllItemRequests(userId, from, size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> getItemRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item requests by user: userId={}", userId);
        Collection<ItemRequestDto> requests = itemRequestService.getItemRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }
}