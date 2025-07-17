package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommentResponseDto {
    private Long id;
    private String text;
    private Instant created;
    private String authorName;
}
