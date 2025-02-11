package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RequestItemsDto {
    private Long id;
    private Long requestId;
    private Long itemId;
    private String itemName;
    private Long ownerId;
    private LocalDateTime created;
}
