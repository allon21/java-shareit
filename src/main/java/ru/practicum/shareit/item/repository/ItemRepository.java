package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private Long itemId = 0L;
    private final HashMap<Long, Item> items = new HashMap<>();


    public Item createItem(Item item) {
        item.setId(++itemId);
        items.put(item.getId(), item);
        return item;
    }

    public Item getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Объект не найден, id = " + itemId);
        }
        return item;
    }

    public Item updateItem(Item newItem) {
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    public Collection<Item> getItems(long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .collect(Collectors.toCollection(HashSet<Item>::new));

    }

    public Collection<Item> searchItems(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) || i.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toCollection(HashSet<Item>::new));
    }
}
