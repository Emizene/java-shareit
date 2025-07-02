package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserResponseDto;

@Data
public class ChangeItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
