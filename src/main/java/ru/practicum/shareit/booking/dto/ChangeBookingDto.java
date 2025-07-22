package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.time.LocalDateTime;

@Data
public class ChangeBookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoSimple item;
    private UserDtoSimple booker;
    private Status status;
}
