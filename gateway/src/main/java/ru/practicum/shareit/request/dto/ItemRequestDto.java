package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;

    @NotNull
    private String description;

    private LocalDateTime created;

    private Collection<ItemResponse> items;
}