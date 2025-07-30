package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;

@Data
public class BookingDtoSimple {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
    private Long bookerId;
}

