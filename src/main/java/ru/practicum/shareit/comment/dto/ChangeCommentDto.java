package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeCommentDto {
    private String text;
}
