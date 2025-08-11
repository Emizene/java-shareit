package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private String ownerName;
    private List<CommentResponseDto> comments;
}
