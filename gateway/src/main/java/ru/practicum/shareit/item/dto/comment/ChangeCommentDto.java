package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeCommentDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
