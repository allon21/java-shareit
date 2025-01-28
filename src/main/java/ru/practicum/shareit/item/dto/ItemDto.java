package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @Size(max = 255)
    private String name;
    @Size(max = 512)
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Collection<CommentDto> comments;
}
