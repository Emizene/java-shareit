package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private String ownerName;
    private List<CommentResponseDto> comments;
}
