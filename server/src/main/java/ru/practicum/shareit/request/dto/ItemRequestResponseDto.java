package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoSimple;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private Instant created;
    private Long requestorId;
    private List<ItemDtoSimple> items;
}
