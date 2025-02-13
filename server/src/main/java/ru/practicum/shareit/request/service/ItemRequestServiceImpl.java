package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.RequestItemsDto;
import ru.practicum.shareit.request.dto.RequestItemsMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.RequestedItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.RequestItemsRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final RequestItemsRepository requestItemsRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Creating item request: userId={}, itemRequestDto={}", userId, itemRequestDto);
        userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestDto);
        itemRequest.setUserId(userId);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getItemRequestsByUser(Long userId) {
        log.info("Getting item requests by user: userId={}", userId);
        userService.getUserById(userId);

        return itemRequestRepository.findByUserIdOrderByCreatedDesc(userId).stream()
                .map(this::loadRequestedItems)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        log.info("Getting item request by ID: requestId={}, userId={}", requestId, userId);
        userService.getUserById(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item request with ID %d not found", requestId))
                );

        return loadRequestedItems(itemRequest);
    }

    @Override
    @Transactional
    public RequestItemsDto createRequestedItem(Item item, ItemRequest itemRequest) {
        log.info("Creating requested item: itemId={}, requestId={}", item.getId(), itemRequest.getId());
        if (item == null || item.getName() == null) {
            throw new IllegalArgumentException("Item or its name cannot be null");
        }
        RequestedItems requestedItems = RequestedItems.builder()
                .item(item)
                .request(itemRequest)
                .created(LocalDateTime.now())
                .build();

        requestedItems = requestItemsRepository.save(requestedItems);
        log.info("Requested item created: id={}", requestedItems.getId());

        return RequestItemsMapper.INSTANCE.toRequestItemsDto(requestedItems);
    }

    @Override
    public ItemRequest findItemRequestById(Long requestId) {
        log.info("Finding item request by ID: requestId={}", requestId);
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item request with ID %d not found", requestId))
                );
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        log.info("Getting all item requests: userId={}, from={}, size={}", userId, from, size);
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestRepository.findByUserIdNotOrderByCreatedDesc(userId, pageable).stream()
                .map(ItemRequestMapper.INSTANCE::toItemRequestDto)
                .toList();
    }

    @Transactional
    private ItemRequestDto loadRequestedItems(ItemRequest itemRequest) {
        log.info("Loading requested items for item request: requestId={}", itemRequest.getId());
        Collection<RequestedItems> requestedItems = requestItemsRepository.findByRequest(itemRequest);

        List<ItemDto> itemDtos = requestedItems.stream()
                .map(requestedItem -> {
                    Item item = requestedItem.getItem();
                    if (item == null) {
                        throw new IllegalStateException("Item not found for requested item");
                    }
                    return ItemMapper.INSTANCE.toItemDto(item);
                }).toList();

        ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemDtos);

        return itemRequestDto;
    }
}