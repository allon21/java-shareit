package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;

    @Size(max = 255)
    @NotNull
    @NotBlank
    private String name;

    @Size(max = 512)
    private String description;

    @NotNull
    private Boolean available;

    private Integer requestId;
}