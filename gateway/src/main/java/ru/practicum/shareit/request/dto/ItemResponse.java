package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {
    private Long itemId;
    private String itemName;
    private Long ownerId;
}
