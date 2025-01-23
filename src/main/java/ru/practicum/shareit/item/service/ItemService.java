package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getAvailable() == null ||
                itemDto.getDescription() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Не все поля объекта заполнены");
        }
    }

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        UserDto user = userService.getUserById(userId);
        validateItem(itemDto);
        Item newItem = itemRepository.createItem(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(newItem);
    }

    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        UserDto user = userService.getUserById(userId);
        Item newItem = itemRepository.getItem(itemId);
        if (!newItem.getOwner().equals(user)) {
            throw new ValidationException("Обновление не владельцем");
        }
        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(newItem));
    }

    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getUserAllItems(Long userId) {
        UserDto user = userService.getUserById(userId);
        return itemRepository.getItems(user.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Collection<ItemDto> searchItems(String text) {
        if (text.isEmpty() || text == null) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
