package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

//@Mapper(componentModel = "spring")
@Component
public class CommentMapper {

    public CommentResponseDto toCommentDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(getAuthorName(comment.getAuthor()))
                .created(comment.getCreated())
                .build();
    }

    public Comment toEntity(ChangeCommentDto changeCommentDto, User author, Item item) {
        return Comment.builder()
                .text(changeCommentDto.getText())
                .author(author)
                .item(item)
                .build();
    }

    private String getAuthorName(User author) {
        return author != null ? author.getName() : null;
    }
}

//    @Mapping(target = "author", ignore = true)
//    Comment toEntity(ChangeCommentDto changeCommentDto);
//
//    @Mapping(target = "authorName", source = "author.name")
//    CommentResponseDto toCommentDto(Comment comment);

