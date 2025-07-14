package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private Long id;
    private String text;
//    private Item item;
    private Instant created;
    private String authorName;
}
