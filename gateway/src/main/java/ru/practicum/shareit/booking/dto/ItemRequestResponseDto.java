package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoSimple;

import java.time.Instant;
import java.util.List;

@Data
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private Instant created;
    private Long requestorId;
    private List<ItemDtoSimple> items;
}
