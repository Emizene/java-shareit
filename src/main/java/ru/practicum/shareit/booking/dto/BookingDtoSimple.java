package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data

public class BookingDtoSimple {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
}
