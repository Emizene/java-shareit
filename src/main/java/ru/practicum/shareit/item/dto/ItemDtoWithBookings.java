package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Data
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
}
