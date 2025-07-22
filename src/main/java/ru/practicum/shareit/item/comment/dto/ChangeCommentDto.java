package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeCommentDto {
    private String text;
}
