package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoSimple nextBooking;
    private BookingDtoSimple lastBooking;
    private List<CommentResponseDto> comments;
    private String ownerName;

    public ItemDtoWithBookings(Long id, String name, String description, Boolean available, String ownerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerName = ownerName;
    }
}
