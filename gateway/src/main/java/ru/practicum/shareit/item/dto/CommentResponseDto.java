package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentResponseDto {
    private Long id;
    private String text;
    private Instant created;
    private String authorName;
}
