package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import ru.practicum.shareit.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(ChangeCommentDto changeCommentDto);

    CommentResponseDto toCommentDto(Comment comment);
}
