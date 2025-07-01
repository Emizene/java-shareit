package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ChangeItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    public ChangeItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
