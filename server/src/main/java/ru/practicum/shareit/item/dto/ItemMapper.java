package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;


@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);
}
