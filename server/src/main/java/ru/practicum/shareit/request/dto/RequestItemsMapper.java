package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.model.RequestedItems;

@Mapper
public interface RequestItemsMapper {
    RequestItemsMapper INSTANCE = Mappers.getMapper(RequestItemsMapper.class);

    RequestedItems toRequestItems(RequestItemsDto requestItemsDto);

    @Mapping(target = "requestId", expression = "java(requestedItems.getRequest().getId())")
    @Mapping(target = "itemId", expression = "java(requestedItems.getItem().getId())")
    @Mapping(target = "itemName", expression = "java(requestedItems.getItem().getName())")
    @Mapping(target = "ownerId", expression = "java(requestedItems.getItem().getOwner().getId())")
    RequestItemsDto toRequestItemsDto(RequestedItems requestedItems);
}