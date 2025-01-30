package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getAvailable() == null ||
                itemDto.getDescription() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Не все поля объекта заполнены");
        }
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        String name = itemDto.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        final String description = itemDto.getDescription();
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Доступность не может быть пустой");
        }
        User owner = UserMapper.INSTANCE.toUser(userService.getUserById(userId));
        Item item = ItemMapper.INSTANCE.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        Item newItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + itemId));
        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.INSTANCE.toItemDto(itemRepository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto deleteItem(Long itemId, Long userId) {
        Item newItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + itemId));
        itemRepository.deleteById(itemId);
        return ItemMapper.INSTANCE.toItemDto(newItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + itemId));

        Collection<Comment> comments = commentRepository.findAllByItem(item);
        ItemDto itemDto = ItemMapper.INSTANCE.toItemDto(item);
        if (comments == null) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream()
                    .map(CommentMapper.INSTANCE::toCommentDto)
                    .toList());
        }
        bookingService.findLastBooking(item)
                .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

        bookingService.findNextBooking(item)
                .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

        return itemDto;
    }

    @Override
    public Collection<ItemDto> getUserAllItems(Long userId) {
        Collection<Item> items = itemRepository.getItemsByOwnerId(userId);
        return items.stream()
                .map(item -> {
                    Collection<Comment> comments = commentRepository.findAllByItem(item);
                    ItemDto itemDto = ItemMapper.INSTANCE.toItemDto(item);
                    itemDto.setComments(comments != null ? comments.stream()
                            .map(CommentMapper.INSTANCE::toCommentDto)
                            .toList() : Collections.emptyList());

                    bookingService.findLastBooking(item)
                            .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

                    bookingService.findNextBooking(item)
                            .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

                    return itemDto;
                })
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long userId) {
        User author = UserMapper.INSTANCE.toUser(userService.getUserById(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + itemId));

        Collection<BookingDto> bookingDto = bookingService.findByItemAndBooker(item, author);

        if (bookingDto.isEmpty()) {
            throw new ValidationException("У объекта нет бронирований");
        }

        Comment comment = CommentMapper.INSTANCE.toComment(commentDto);
        comment.setItem(item);
        comment.setUser(author);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    private ItemDto populateItemDto(Item item) {
        Collection<Comment> comments = commentRepository.findAllByItem(item);
        ItemDto itemDto = ItemMapper.INSTANCE.toItemDto(item);
        if (comments == null || comments.isEmpty()) {
            itemDto.setComments(Collections.emptyList());
        } else {
            itemDto.setComments(comments.stream()
                    .map(CommentMapper.INSTANCE::toCommentDto)
                    .collect(Collectors.toList()));
        }

        bookingService.findLastBooking(item)
                .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

        bookingService.findNextBooking(item)
                .ifPresent(booking -> itemDto.setNextBooking(BookingMapper.INSTANCE.toBookingDto(booking)));

        return itemDto;
    }

}
