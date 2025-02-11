package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getItemRequestsByUser(Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);

    RequestItemsDto createRequestedItem(Item item, ItemRequest itemRequest);

    ItemRequest findItemRequestById(Long requestId);

    Collection<ItemRequestDto> getAllItemRequests(Long userId);
}