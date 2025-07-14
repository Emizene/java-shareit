package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", ignore = true)
    Comment toEntity(ChangeCommentDto changeCommentDto);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toCommentDto(Comment comment);
}
