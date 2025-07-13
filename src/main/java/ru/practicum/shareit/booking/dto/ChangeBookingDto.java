package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.SimpleItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ChangeBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private SimpleItemDto item;
    private User booker;
    private Status status;
}
