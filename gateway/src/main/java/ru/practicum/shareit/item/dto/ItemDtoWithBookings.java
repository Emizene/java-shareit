package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;

import java.util.List;

@Data
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