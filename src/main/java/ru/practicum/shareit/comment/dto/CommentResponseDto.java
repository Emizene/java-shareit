package ru.practicum.shareit.comment.dto;

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
