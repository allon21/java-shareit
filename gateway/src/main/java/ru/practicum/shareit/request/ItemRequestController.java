package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto.setUserId(userId);
        log.info("Create item request: userId={}, itemRequestDto={}", userId, itemRequestDto);
        return requestClient.createItemRequest(itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all item requests by user: userId={}", userId);
        return requestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all item requests: userId={}", userId);
        return requestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("Get item request by ID: userId={}, requestId={}", userId, requestId);
        return requestClient.getItemRequestById(requestId, userId);
    }
}